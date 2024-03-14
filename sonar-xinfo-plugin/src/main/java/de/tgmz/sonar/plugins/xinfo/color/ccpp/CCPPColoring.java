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
package de.tgmz.sonar.plugins.xinfo.color.ccpp;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

import de.tgmz.sonar.plugins.xinfo.color.DefaultColoring;

/**
 * Syntax highlighting for C/C++ files.
 */
public class CCPPColoring extends DefaultColoring {
	private static final List<String> CCPP_KEYWORDS;
	private static final Pattern CCPP_COMMENT_PATTERN = Pattern.compile("\\/\\*.*\\*\\/");
	private static final Pattern CCPP_PREPROCESS_PATTERN = Pattern.compile("#\\w+");
	private static final Pattern CCPP_STRING_PATTERN = Pattern.compile("[\"'].*[\"']");
	private static final Pattern CCPP_WORD_PATTERN = Pattern.compile("[\\w$§\\.]+");		//Underscore IS a word character

	static {
		// Keywords
		CCPP_KEYWORDS = new ArrayList<>(ReservedWords.values().length);
		
		for (ReservedWords rw : ReservedWords.values()) {
			CCPP_KEYWORDS.add(rw.toString());
		}
	}
	
	public CCPPColoring(InputFile file, Charset charset, int limit) throws IOException {
		super(file, charset, limit);
	}

	@Override
	public void createAreas() {
		// Comments
		colorAreaByPattern(CCPP_COMMENT_PATTERN, TypeOfText.COMMENT);

		//Multiline comments
		//Not yet implemented
		
		// Preprocessor directives (#include, #line etc.)
		colorAreaByPattern(CCPP_PREPROCESS_PATTERN, TypeOfText.PREPROCESS_DIRECTIVE);
		
		// Strings
		colorAreaByPattern(CCPP_STRING_PATTERN, TypeOfText.STRING);

		//multiline strings
		//Not yet implemented
		
		//Reserved words
		colorTokens(CCPP_WORD_PATTERN,  Collections.singletonMap(TypeOfText.KEYWORD, CCPP_KEYWORDS), -1, Integer.MAX_VALUE);
	}
}
