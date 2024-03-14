/*******************************************************************************
  * Copyright (c) 13.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.color.assembler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

import de.tgmz.sonar.plugins.xinfo.color.ColoringData;
import de.tgmz.sonar.plugins.xinfo.color.DefaultColoring;

/**
 * Syntax highlighting for Assembler files.
 */
public class AssemblerColoring extends DefaultColoring {
	private static final int DFT_LIMIT = 40;
	private static final List<String> INSTRUCTIONS;
	private static final List<String> BUILTIN;
	private static final Pattern ASSEMBLER_WORD_PATTERN = Pattern.compile("[\\w$§\\.]+");
	private static final Pattern ASSEMBLER_STRING_PATTERN = Pattern.compile("[\"'].*[\"']");
	// Lines starting with an asterisk are comments
	private static final Pattern ASSEMBLER_COMMENT_PATTERN_1 = Pattern.compile("^\\*.*$");
	// A blank at column 39 followed by a word-character indicates that the rest of the line is a comment
	private static final Pattern ASSEMBLER_COMMENT_PATTERN_2 = Pattern.compile("^.{" + (DFT_LIMIT-2) + "} \\w+.*$");

	static {
		// Instructions
		INSTRUCTIONS = new ArrayList<>(Instructions.values().length);
		
		for (Instructions ins : Instructions.values()) {
			INSTRUCTIONS.add(ins.toString());
		}
		
		// Instructions
		for (Instructions370 ins : Instructions370.values()) {
			INSTRUCTIONS.add(ins.toString());
		}
		
		// Instructions
		BUILTIN = new ArrayList<>(Builtin.values().length);
		
		for (Builtin bi : Builtin.values()) {
			BUILTIN.add(bi.toString());
		}
	}
	
	public AssemblerColoring(InputFile file, Charset charset, int limit) throws IOException {
		super(file, charset, limit);
	}

	@Override
	public void createAreas() {
		// Comments
		colorComments();
		
		// Strings
		colorAreaByPattern(ASSEMBLER_STRING_PATTERN, TypeOfText.STRING);

		//Multiline strings
		//Not yet implemented
		
		//Reserved words
		Map<TypeOfText, List<String>> colorTokens = new TreeMap<>();
		colorTokens.put(TypeOfText.KEYWORD, INSTRUCTIONS);
		colorTokens.put(TypeOfText.KEYWORD_LIGHT, BUILTIN);

		colorTokens(ASSEMBLER_WORD_PATTERN, colorTokens, -1, Integer.MAX_VALUE);
	}
	
	private void colorComments() {
		for (int i = 0; i < getContent().length; ++i) {
			Matcher m = ASSEMBLER_COMMENT_PATTERN_1.matcher(getContent()[i]);
		
			while (m.find()) {
				String s = getContent()[i].substring(m.start(), m.end());
			
				getAreas().add(new ColoringData(i+1, m.start(), i+1, m.end(), s, TypeOfText.COMMENT));
			}
		}
		
		for (int i = 0; i < getContent().length; ++i) {
			Matcher m = ASSEMBLER_COMMENT_PATTERN_2.matcher(getContent()[i]);
		
			while (m.find()) {
				String line = getContent()[i];
			
				getAreas().add(new ColoringData(i+1, DFT_LIMIT - 1, i+1, line.length(), line.substring(DFT_LIMIT - 1), TypeOfText.COMMENT));
			}
		}
	}
}
