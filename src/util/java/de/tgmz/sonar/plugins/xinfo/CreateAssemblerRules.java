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

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;

import de.tgmz.sonar.plugins.xinfo.generated.Rule;
import de.tgmz.sonar.plugins.xinfo.generated.Tag;

/**
 * Generates assembler-rules.xml.
 */
public class CreateAssemblerRules {
	public static void main(String[] args) throws Exception {
		new CreateAssemblerRules().perform();
	}
	
	private void perform() throws Exception {
		Set<String> set = new HashSet<>();
		
		PrintWriter pw = new PrintWriter("src/main/resources/assembler-rules.xml", StandardCharsets.UTF_8.name());
		
		pw.println("<xinfo-rules>");
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Rule.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

		List<String> l = IOUtils.readLines(new FileInputStream("ibm/Assembler/asmp1021.txt"), StandardCharsets.UTF_8);
		
		StringBuilder sb = new StringBuilder();
		
		for (String s0 : l) {
			if (!(s0.startsWith("Appendix F. High Level Assembler messages")	// Copyright
					|| s0.contains(" • ")				// Überschrift
					|| s0.contains("High Level Assembler for z/OS & z/VM & z/VSE: Programmer's Guide"))) {	// Footer
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
		
		Pattern p = Pattern.compile("ASMA\\d{3}[IWESU]");
		
		Matcher m = p.matcher(s);
		
		m.find();
		int sta = m.start();
		
		while (m.find()) {
			int end = m.start();
			String msg = s.substring(sta, end);
			
			String key = msg.substring(0, 8);
			
			if (!set.add(key)) {
				sta = end;
				
				continue;
			}
			
			String sev = msg.substring(7, 8);
			
			Rule r = new Rule();
			r.setCardinality("SINGLE");
			r.setInternalKey(key);
			r.setKey(key);
			r.setStatus("READY");
			Tag tag = new Tag(); tag.setvalue("xinfo"); r.getTag().add(tag);
			
			String name = s.substring(sta + 9, end).trim();
			r.setName(name.substring(0, Math.min(name.length(), 200))); // VARCHAR(200) in DB
			
			r.setDescription(name);
			
			switch (sev) {
				case "I": r.setSeverity("MINOR"); break;
				case "W": r.setSeverity("MAJOR"); break;
				case "E": r.setSeverity("CRITICAL"); break;
				default: r.setSeverity("BLOCKER"); break;
			}
			
			jaxbMarshaller.marshal(r, pw);
			
			pw.println();
			
			sta = end;
		}
		
		pw.println("</xinfo-rules>");
		
		pw.close();
	}
}
