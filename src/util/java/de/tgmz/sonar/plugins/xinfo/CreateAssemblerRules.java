/*******************************************************************************
  * Copyright (c) 18.01.2022 Thomas Zierer.
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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import jakarta.xml.bind.JAXBException;

/**
 * Generates assembler-rules.xml.
 */
public class CreateAssemblerRules extends AbstractRuleCreator {
	public CreateAssemblerRules(String documentation, String output) {
		super(documentation, output);
	}

	public static void main(String... args) throws IOException, JAXBException {
		new CreateAssemblerRules(args[0], args[1]).perform();
	}
	
	private void perform() throws JAXBException, IOException {
		open();

		PDDocument asmp1021 = PDDocument.load(getDocumentation());
		
		PDFTextStripper pdfts = new PDFTextStripper();
		
		List<String> l0 = IOUtils.readLines(new StringReader(pdfts.getText(asmp1021)));

		// Cut out relevant part of the "Appendix F" section
		int i = 0;
		
		for (; i < l0.size(); i++) {
			if (l0.get(i).startsWith("Appendix F. High Level Assembler messages")) {
				break;
			}
		}
		
		for (; i < l0.size(); i++) {
			if ("Messages".equals(l0.get(i))) {
				break;
			}
		}

		int j = i;

		for (; j < l0.size(); j++) {
			if (l0.get(j).startsWith("Appendix G. User interface macros")) {
				break;
			}
		}
		
		List<String> l = new ArrayList<>(l0.subList(i, j));
		
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
		
		for (String msg : getSections(s, "ASMA\\d{3}[IWESU]")) {
			String key = msg.substring(0, 8);
			
			createRule(key, msg.substring(9).trim());
		}
		
		close();
		
		asmp1021.close();
	}
}
