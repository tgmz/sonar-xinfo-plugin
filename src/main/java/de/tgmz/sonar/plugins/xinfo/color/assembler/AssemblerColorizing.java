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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

import de.tgmz.sonar.plugins.xinfo.color.AbstractColorizing;
import de.tgmz.sonar.plugins.xinfo.color.ColorizingData;

/**
 * Syntax highlighting for Assembler files.
 */
public class AssemblerColorizing extends AbstractColorizing {
	private static final int DFT_LIMIT = 40;
	private static final List<String> INSTRUCTIONS;
	private static final List<String> BUILTIN;
	private static final Pattern ASSEMBLER_WORD_PATTERN = Pattern.compile("[\\w$§\\.]+");
	private static final Pattern ASSEMBLER_STRING_PATTERN = Pattern.compile("[\"'].*[\"']");

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
	
	public AssemblerColorizing(InputFile file, int limit) throws IOException {
		super(file, limit);
	}

	@Override
	protected void createAreas() {
		// Comments
		colorizeComments();
		
		// Strings
		//TODO: Multiline strings
		colorizeAreaByPattern(ASSEMBLER_STRING_PATTERN, TypeOfText.STRING);

		for (int i = 0; i < getContent().length; ++i) {
			// Split the text by word characters and highlight keywords and numeric constants
			Matcher m = ASSEMBLER_WORD_PATTERN.matcher(getContent()[i]);
			
			while (m.find()) {
				String token = getContent()[i].substring(m.start(), m.end());
				
				colorizeToken(i+1, m.start(), m.end(), token);
			}
		}
	}

	private void colorizeToken(int lineNumber, int startOffset, int endOffset, String token) {
		if (INSTRUCTIONS.contains(token.toUpperCase(Locale.US))) {			
			getAreas().add(new ColorizingData(lineNumber, startOffset, lineNumber, endOffset, token, TypeOfText.KEYWORD));
		} else {
			if (BUILTIN.contains(token.toUpperCase(Locale.US))) {			
				getAreas().add(new ColorizingData(lineNumber, startOffset, lineNumber, endOffset, token, TypeOfText.KEYWORD_LIGHT));
			} else {
				if (NumberUtils.isNumber(token)) {
					getAreas().add(new ColorizingData(lineNumber, startOffset, lineNumber, endOffset, token, TypeOfText.CONSTANT));
				}
			}
		}
	}
	
	private void colorizeComments() {
		// Lines starting with an asterisk are comments
		Pattern p0 = Pattern.compile("^\\*.*$");
		
		for (int i = 0; i < getContent().length; ++i) {
			Matcher m = p0.matcher(getContent()[i]);
		
			while (m.find()) {
				String s = getContent()[i].substring(m.start(), m.end());
			
				getAreas().add(new ColorizingData(i+1, m.start(), i+1, m.end(), s, TypeOfText.COMMENT));
			}
		}
		
		// A blank at column 39 followed by a word-character
		// indicates that the rest of the line is a comment
		p0 = Pattern.compile("^.{" + (DFT_LIMIT-2) + "} \\w+.*$");
		
		for (int i = 0; i < getContent().length; ++i) {
			Matcher m = p0.matcher(getContent()[i]);
		
			while (m.find()) {
				String line = getContent()[i];
			
				getAreas().add(new ColorizingData(i+1, DFT_LIMIT - 1, i+1, line.length(), line.substring(DFT_LIMIT - 1), TypeOfText.COMMENT));
			}
		}
	}
}
