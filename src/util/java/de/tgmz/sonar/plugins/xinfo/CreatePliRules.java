/*******************************************************************************
  * Copyright (c) 09.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo;

import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.sonar.api.rule.RuleStatus;

/**
 * Generates pli-rules.xml.
 */
public class CreatePliRules {
	private PrintWriter pw;
	private Set<String> rules;
	private Marshaller jaxbMarshaller;
	
	public static void main(String[] args) throws Exception {
		new CreatePliRules().perform();
	}
	
	private void perform() throws Exception {
		rules = new HashSet<>();	// Duplikate vermeiden
		
		PDDocument ibmPliMessagesAndCodes = PDDocument.load("ibm/pli/Messages and Codes.pdf");
		
		PDFTextStripper pdfts = new PDFTextStripper();
		
		String ibmPliMessagesAndCodesAsText = pdfts.getText(ibmPliMessagesAndCodes);
		
		pw = new PrintWriter("src/main/resources/pli-rules.xml", StandardCharsets.UTF_8.name());
		
		pw.println("<xinfo-rules>");
		
		JAXBContext jaxbContext = JAXBContext.newInstance(SonarRule.class);
		jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

		List<String> l = IOUtils.readLines(new StringReader(ibmPliMessagesAndCodesAsText));
		
		StringBuilder sb = new StringBuilder();
		
		for (String s0 : l) {
			if (!(s0.startsWith("© Copyright IBM Corp.")	// Copyright
					|| s0.contains(" • ")					// Überschrift
					|| s0.contains("Enterprise PL/I for z/OS Messages and Codes"))) {	// Footer
				try {
					Integer.parseInt(s0.trim());			// Seitenzahl?
					
					continue;
				} catch (NumberFormatException e) {
					// Okay
				}
				sb.append(s0);
				sb.append(' ');
			}
		}
		
		String s = sb.toString();
		
		Pattern p = Pattern.compile("IBM\\d{4}I\\s[IWESU]");
		
		Matcher m = p.matcher(s);
		
		m.find();
		int sta = m.start();
		
		while (m.find()) {
			int end = m.start();
			
			createRule(s, sta, end);
			
			sta = end;
		}
		
		pw.println("</xinfo-rules>");
		
		pw.close();
	}
	
	private void createRule(String s, int sta, int end) throws JAXBException {
		String msg = s.substring(sta, end);
		
		String key = msg.substring(0, 10);
		
		if (!rules.add(key)) {
			return;
		}
		
		String sev = msg.substring(9, 10);
		
		SonarRule r = new SonarRule();
		r.setCardinality("SINGLE");
		r.setInternalKey(key);
		r.setKey(key);
		r.setStatus(RuleStatus.READY.toString());
		r.setTag(Collections.singletonList("xinfo"));
		r.setRemediationFunction("CONSTANT_ISSUE");
		r.setRemediationFunctionBaseEffort("0d 0h 10min");
		
		int desc = s.indexOf("Explanation: ", sta);
		r.setDescription(s.substring(desc + "Explanation: ".length(), end));
		
		String name = s.substring(sta + 11, desc);
		
		r.setName(name.substring(0, Math.min(name.length(), 200))); // VARCHAR(200) in DB
		
		switch (r.getKey()) {
		case "IBM1035I I":	// The next statement was merged with this statement.
		case "IBM1036I I":	// The next statement-count statements were merged with this statement.
		case "IBM1041I I":	// Comment spans line-count lines.
		case "IBM3020I I":	// Comment spans line-count lines.
			r.setSeverity("INFO");
			break;
		case "IBM2812I I":	// Argument number argument number to BUILTIN name built-in would lead to much better code if declared with the VALUE attribute
			r.setSeverity("MAJOR");
			r.setTag(Arrays.asList("xinfo", "performance"));
			break;
		case "IBM1208I W":	// INITIAL list for the array variable name contains only one item.
		case "IBM2603I W":	// INITIAL list for the array variable name contains only one item.
			r.setSeverity("MAJOR");
			r.setType("BUG");
			break;
		case "IBM1063I I":	// Code generated for DO group would be more efficient if control variable were a 4-byte integer.
			r.setSeverity("MINOR");
			r.setTag(Arrays.asList("xinfo", "performance"));
		default:
			switch (sev) {
			case "I": r.setSeverity("MINOR"); break;
			case "W": r.setSeverity("MAJOR"); break;
			case "E": r.setSeverity("CRITICAL"); r.setType("BUG"); break;
			default: r.setSeverity("BLOCKER"); r.setType("BUG"); break;
			}
		}
		
		jaxbMarshaller.marshal(r, pw);
		
		pw.println();
		
	}

}
