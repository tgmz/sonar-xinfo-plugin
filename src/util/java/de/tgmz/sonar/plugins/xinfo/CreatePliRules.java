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

import de.tgmz.sonar.plugins.xinfo.generated.Rule;
import de.tgmz.sonar.plugins.xinfo.generated.Tag;

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
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Rule.class);
		jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

		List<String> l = IOUtils.readLines(new StringReader(ibmPliMessagesAndCodesAsText));
		
		StringBuilder sb = new StringBuilder();
		
		for (String s0 : l) {
			if (!(s0.startsWith("© Copyright IBM Corp.")	// Copyright
					|| s0.contains(" • ")					// Überschrift
					|| s0.contains(".....") 				// Contents
					|| s0.contains("Enterprise PL/I for z/OS"))) {	// Footer
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
		
		String key = "IBM2671I W";
		
		if (!rules.contains(key)) {
			/* Undocumented */
			String s0 = "The variable var is passed as argument number count to entry entry. The corresponding parameter has the ASSIGNABLE attribute, and hence the variable could be modified despite having the NONASSIGNABLE attribute.";
			
			Rule r = createDefaults(key, s0);
			r.setDescription(s0);
			r.setSeverity("MAJOR");
				
			jaxbMarshaller.marshal(r, pw);
		
			pw.println();
		}
		
		key = "IBM2847I I";
		
		if (!rules.contains(key)) {
			/* Undocumented */
			String s0 = "Source in RETURN statement has a MAXLENGTH of lenght which is greater than the length of length in the corresponding RETURNS attribute";
			
			Rule r = createDefaults(key, s0);
			r.setDescription(s0);
			r.setSeverity("MINOR");
				
			jaxbMarshaller.marshal(r, pw);
		
			pw.println();
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
		
		int desc = s.indexOf("Explanation ", sta);
		int suffix = s.indexOf("Codes Chapter", sta);
		
		Rule r = createDefaults(key, s.substring(sta + 11, desc));
		
		r.setDescription(s.substring(desc + "Explanation ".length(), suffix));
		
		switch (r.getKey()) {
		case "IBM1035I I":	// The next statement was merged with this statement.
		case "IBM1036I I":	// The next statement-count statements were merged with this statement.
		case "IBM1041I I":	// Comment spans line-count lines.
		case "IBM3020I I":	// Comment spans line-count lines.
		case "IBM1214I W":	// A dummy argument will be created for argument argument
		case "IBM3000I I":	// This message is used to report DB2 or CICS backend messages with a return code of 0.
			r.setSeverity("INFO");
			break;
		case "IBM2812I I":	// Argument number argument number to BUILTIN name built-in would lead to much better code if declared with the VALUE attribute
			r.setSeverity("MAJOR");
			Tag tag0 = new Tag(); tag0.setvalue("performance"); r.getTag().add(tag0);
			break;
		case "IBM1208I W":	// INITIAL list for the array variable name contains only one item.
		case "IBM2603I W":	// INITIAL list for the array variable name contains only one item.
			r.setSeverity("MAJOR");
			r.setType("BUG");
			break;
		case "IBM1059I I":	//SELECT statement contains no OTHERWISE clause
		case "IBM1060I I":	//Name resolution for identifier selected its declaration in a structure, rather than its non-member declaration in a parent block.
			r.setSeverity("MAJOR");
			break;
		case "IBM2804I I":	//Boolean is compared with something other than '1'b or '0'b.	
			r.setSeverity("CRITICAL");
			r.setType("BUG");
			break;
		case "IBM1063I I":	// Code generated for DO group would be more efficient if control variable were a 4-byte integer.
			r.setSeverity("MINOR");
			Tag tag1 = new Tag(); tag1.setvalue("performance"); r.getTag().add(tag1);
			break;
		case "IBM2402I E":	//variable name is declared as BASED on the ADDR of variable name,
							//but variable name requires more storage than variable name.
		case "IBM2409I E":	//RETURN statement without an expression is invalid inside a subprocedure that specified the RETURNS attribute.
		case "IBM2452I E":	//Scale factor is less than 0.  
		case "IBM1247I E":	//Arithmetic operands should both be numeric  
		case "IBM1482I E":	//The variable variable name is declared without any data attributes  
		case "IBM1373I E":	//Variable variable name is implicitly declared  
		case "IBM1274I E":	//RULES(NOLAXIF) requires BIT(1) expressions in IF, WHILE, etc  
		case "IBM2436I E":	//Scale factor is larger than the precision  
			r.setSeverity("BLOCKER");
			break;
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
	private Rule createDefaults(String key, String name) {
		Rule r = new Rule();
		r.setCardinality("SINGLE");
		r.setInternalKey(key);
		r.setKey(key);
		r.setStatus(RuleStatus.READY.toString());
		Tag tag = new Tag(); tag.setvalue("xinfo"); r.getTag().add(tag);
		r.setRemediationFunction("CONSTANT_ISSUE");
		r.setRemediationFunctionBaseEffort("0d 0h 10min");
		r.setName(name.substring(0, Math.min(name.length(), 200))); // VARCHAR(200) in DB
		
		return r;
	}
}
