/*******************************************************************************
  * Copyright (c) 14.12.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.maven.plugin.xinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class XinfoMojo extends AbstractMojo {
	private String ruleTemplate;
	private Set<String> rules = new HashSet<>();
			
	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;
	@Parameter(defaultValue = "de.tgmz.xinfo.rules", property = "targetPackage", required = true)
	private String targetPackage;
	@Parameter(defaultValue = "ibm/Assembler/asmp1021.pdf", property = "document", required = true)
	private File document;
	@Parameter(defaultValue = "asm", property = "lang", required = true)
	private String lang;

	@Override
	public void execute() throws MojoExecutionException {
		try {
			ruleTemplate = IOUtils.resourceToString("rule.txt", StandardCharsets.UTF_8, this.getClass().getClassLoader());
			
			//Replace license header by a "generated" message 
			int idx = ruleTemplate.indexOf("package");
			ruleTemplate = "//This file was gereated by " + this.getClass().getName() + ". Do not edit!"
					+ System.lineSeparator()
					+ ruleTemplate.substring(idx);
			
			File f = outputDirectory;

			if (!f.exists()) {
				f.mkdirs();
			}
			
			switch (lang) {
			case "pli":
				generatePli();
				break;
			case "cobol":
				generateCobol();
				break;
			case "asm":
				generateAssembler();
				break;
			case "ccpp":
				generateCcpp();
				break;
			default:
				throw new MojoExecutionException("The language " + lang + " is not supported");
			}
			
		} catch (IOException e) {
			getLog().error(e);
		}
	}
	private void createRule(String key, String target, char sev, String name) throws IOException {
		createRule(key, target, sev, name, name);
	}
	
	private void createRule(String key, String target, char sev, String name, String description) throws IOException {
		if (rules.contains(key)) {
			getLog().info("Rule " + key +" already added, skipping");

			return;
		}
		
		rules.add(key);
		
		String priority;
		
		switch (sev) {
		case 'I':
			priority = "Priority.INFO";
			break;
		case 'W':
			priority = "Priority.MINOR";
			break;
		case 'E':
			priority = "Priority.MAJOR";
			break;
		case 'S':
			priority = "Priority.CRITICAL";
			break;
		case 'U':
		default:
			priority = "Priority.BLOCKER";
			break;
		}

		String rule = MessageFormat.format(ruleTemplate, key, name.substring(0, Math.min(name.length(), 200)), description, target, priority);
		
		File out = new File(outputDirectory, targetPackage.replace('.', File.separatorChar));
		
		if (!out.exists()) {
			out.mkdirs();
		}
	
		PrintWriter pw = new PrintWriter(new File(out, key + ".java"));
		pw.write(rule);
		pw.close();

	}
	
	private List<String> getSections(String documentation, String msgPattern) {
		List<String> result = new LinkedList<>();
		
		Pattern p = Pattern.compile(msgPattern);
		
		Matcher m = p.matcher(documentation);
		
		m.find();
		int sta = m.start();
		
		while (m.find()) {
			int end = m.start();
			result.add(documentation.substring(sta, end));
			
			sta = end;
		}

		return result;
	}
	private void generatePli() throws IOException {
		PDDocument ibmPliMessagesAndCodes = PDDocument.load(document);
		
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
			String key = msg.substring(0, 8);
			char sev = msg.charAt(9);
			
			int desc = msg.indexOf("Explanation ");
			int suffix = msg.indexOf("Codes Chapter");
			
			String name = msg.substring(11, desc);
			String description = msg.substring(desc + "Explanation ".length(), suffix);
			description = description.replace("\"", "");
			
			createRule(key, targetPackage, sev, name, description);
		}
		
		// Undocumented
		createRule("IBM2671I", targetPackage, 'W', "The variable var is passed as argument number count to entry entry. The corresponding parameter has the ASSIGNABLE attribute, and hence the variable could be modified despite having the NONASSIGNABLE attribute.");
		createRule("IBM2847I", targetPackage, 'I', "Source in RETURN statement has a MAXLENGTH of lenght which is greater than the length of length in the corresponding RETURNS attribute");
		createRule("IBM2848I", targetPackage, 'I', "ADD of FIXED DEC(p0,q0) and FIXED DEC(p1,q1) with a result precision and scale of (p2,q2) might overflow.");
		
		ibmPliMessagesAndCodes.close();
	}
	private void generateCobol() throws IOException {
		try(InputStream is = new FileInputStream(document)) {
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
				String key = msg.substring(0, 9);
				char sev = msg.charAt(10);
				String name = msg.substring(16).trim().replace("\"", "");
				
				createRule(key, targetPackage, sev, name);
			}
		}
	}
	private void generateAssembler() throws IOException {
		PDDocument asmp1021 = PDDocument.load(document);
		
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
					|| s0.contains(" • ")				// Heading
					|| s0.contains("High Level Assembler for z/OS & z/VM & z/VSE: Programmer's Guide"))) {	// Footer
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
		
		for (String msg : getSections(s, "ASMA\\d{3}[IWESU]")) {
			String key = msg.substring(0, 7);
			char sev = msg.charAt(7);
			String name = msg.substring(9).trim().replace("\"", "");
			
			createRule(key, targetPackage, sev, name);
		}
		
		asmp1021.close();
	}
	private void generateCcpp() throws IOException {
		PDDocument cbcdg01 = PDDocument.load(document);
		
		PDFTextStripper pdfts = new PDFTextStripper();
		
		List<String> l0 = IOUtils.readLines(new StringReader(pdfts.getText(cbcdg01)));

		// Cut out relevant part of the "Appendix F" section
		int i = 0;
		
		for (; i < l0.size(); i++) {
			if (l0.get(i).startsWith("Chapter 2. z/OS XL C/C++ compiler return codes and messages")) {
				break;
			}
		}
		
		for (; i < l0.size(); i++) {
			if ("Compiler messages".equals(l0.get(i))) {
				break;
			}
		}

		int j = i;

		for (; j < l0.size(); j++) {
			if (l0.get(j).startsWith("Chapter 3. Utility messages")) {
				break;
			}
		}
		
		List<String> l = new ArrayList<>(l0.subList(i, j));
		
		StringBuilder sb = new StringBuilder();
		
		for (String s0 : l) {
			if (!s0.startsWith("z/OS: z/OS XL C/C++ Messages")) { // Copyright
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
		
		for (String msg : getSections(s, "CCN\\d{4}")) {
			String key = msg.substring(0, 7);
			char sev = 'I';
			String name = msg.substring(8).trim().replace("\"", "").replace("\\", "");
			
			createRule(key, targetPackage, sev, name);
		}
		
		cbcdg01.close();
	}
	public void setDocument(File document) {
		this.document = document;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
}
