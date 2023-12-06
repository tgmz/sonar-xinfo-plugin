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

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import jakarta.xml.bind.JAXBException;

/**
 * Generates pli-rules.xml.
 */
public class CreatePliRules extends AbstractRuleCreator {
	public CreatePliRules(String documentation, String output) {
		super(documentation, output);
	}
	
	public static void main(String... args) throws IOException, JAXBException {
		new CreatePliRules(args[0], args[1]).perform();
	}
	
	private void perform() throws IOException, JAXBException {
		open();
		
		PDDocument ibmPliMessagesAndCodes = PDDocument.load(getDocumentation());
		
		PDFTextStripper pdfts = new PDFTextStripper();
		
		String ibmPliMessagesAndCodesAsText = pdfts.getText(ibmPliMessagesAndCodes);
		
		List<String> l = IOUtils.readLines(new StringReader(ibmPliMessagesAndCodesAsText));
		
		StringBuilder sb = new StringBuilder();
		
		for (String s0 : l) {
			if (!(s0.startsWith("© Copyright IBM Corp.")	// Copyright
					|| s0.contains(" • ")					// Header
					|| s0.contains(".....") 				// Contents
					|| s0.contains("Enterprise PL/I for z/OS"))) {	// Footer
				try {
					Integer.parseInt(s0.trim());			// Page?
					
					continue;
				} catch (NumberFormatException e) {
					// Okay
				}
				sb.append(s0);
				sb.append(' ');
			}
		}
		
		String s = sb.toString();
		
		for (String msg : getSections(s, "IBM\\d{4}I\\s[IWESU]")) {
			String key = msg.substring(0, 10);
			
			int desc = msg.indexOf("Explanation ");
			int suffix = msg.indexOf("Codes Chapter");
			
			createRule(key, msg.substring(11, desc), msg.substring(desc + "Explanation ".length(), suffix));
		}

		// Undocumented
		createRule("IBM2671I W", "The variable var is passed as argument number count to entry entry. The corresponding parameter has the ASSIGNABLE attribute, and hence the variable could be modified despite having the NONASSIGNABLE attribute.");
		
		createRule("IBM2847I I", "Source in RETURN statement has a MAXLENGTH of lenght which is greater than the length of length in the corresponding RETURNS attribute");
			
		createRule("IBM2848I I", "ADD of FIXED DEC(p0,q0) and FIXED DEC(p1,q1) with a result precision and scale of (p2,q2) might overflow.");

		close();
		
		ibmPliMessagesAndCodes.close();
	}

}
