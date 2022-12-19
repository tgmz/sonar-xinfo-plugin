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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;

import jakarta.xml.bind.JAXBException;

/**
 * Generates assembler-rules.xml-
 */
public class CreateCobolRules extends AbstractRuleCreator {
	public CreateCobolRules(String documentation, String output) {
		super(documentation, output);
	}

	public static void main(String... args) throws IOException, JAXBException {
		new CreateCobolRules(args[0], args[1]).perform();
	}
	
	private void perform() throws IOException, JAXBException {
		open();
		
		try(InputStream is = new FileInputStream(getDocumentation())) {
			List<String> l = IOUtils.readLines(is, StandardCharsets.UTF_8);
			
			StringBuilder sb = new StringBuilder();
			
			for (String s0 : l) {
				if (!(s0.startsWith("1PP 5655-EC6") || s0.trim().length() == 1)) {	// Page header
					sb.append(s0.substring(1).trim());
					sb.append(' ');
				}
			}
			
			String s = sb.toString();
			
			for (String msg : getSections(s, "IGYXX\\d{4}\\-[IWESU]")) {
				String key = msg.substring(0, 11);
				String name = msg.substring(16).trim();
				
				createRule(key, name);
			}
		}
		
		close();
	}
}
