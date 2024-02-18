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
package de.tgmz.sonar.plugins.xinfo.color.pli;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

import de.tgmz.sonar.plugins.xinfo.color.AbstractColorizing;

/**
 * Syntax highlighting for PL/I files.
 */
public class PliColorizing extends AbstractColorizing {
	private static final List<String> PLI_KEYWORDS;
	private static final List<String> PLI_BUILTIN;
	private static final Pattern PLI_COMMENT_PATTERN = Pattern.compile("\\/\\*.*\\*\\/");
	private static final Pattern PLI_PREPROCESS_PATTERN = Pattern.compile("%\\w+");
	private static final Pattern PLI_STRING_PATTERN = Pattern.compile("[\"'].*[\"']");
	private static final Pattern PLI_WORD_PATTERN = Pattern.compile("[\\w$§\\.]+");		//Underscore IS a word character

	static {
		// Keywords
		PLI_KEYWORDS = new ArrayList<>(ReservedWords.values().length);
		
		for (ReservedWords rw : ReservedWords.values()) {
			PLI_KEYWORDS.add(rw.toString());
		}
		
		// Builtin functions
		PLI_BUILTIN = new ArrayList<>(Builtin.values().length);
		
		for (Builtin b : Builtin.values()) {
			PLI_BUILTIN.add(b.toString());
		}
	}
	
	public PliColorizing(InputFile file, Charset charset, int limit) throws IOException {
		super(file, charset, limit);
	}

	@Override
	protected void createAreas() {
		// Comments
		colorizeAreaByPattern(PLI_COMMENT_PATTERN, TypeOfText.COMMENT);

		//Multiline comments
		//Not yet implemented
		
		// Preprocessor directives (%INCLUDE, %DCL, etc.)
		colorizeAreaByPattern(PLI_PREPROCESS_PATTERN, TypeOfText.PREPROCESS_DIRECTIVE);
		
		// Strings
		colorizeAreaByPattern(PLI_STRING_PATTERN, TypeOfText.STRING);

		//multiline strings
		//Not yet implemented
		
		//Reserved words
		Map<TypeOfText, List<String>> colorTokens = new TreeMap<>();
		colorTokens.put(TypeOfText.KEYWORD, PLI_KEYWORDS);
		colorTokens.put(TypeOfText.KEYWORD_LIGHT, PLI_BUILTIN);

		colorizeTokens(PLI_WORD_PATTERN, colorTokens, -1, 71);
	}
}
