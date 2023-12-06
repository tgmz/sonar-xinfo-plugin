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
 * Syntax highlighting for PL/I files.
 */
public class CCPPColorizing extends AbstractColorizing {
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
	
	public CCPPColorizing(InputFile file, Charset charset, int limit) throws IOException {
		super(file, charset, limit);
	}

	@Override
	protected void createAreas() {
		// Comments
		colorizeAreaByPattern(CCPP_COMMENT_PATTERN, TypeOfText.COMMENT);

		//Multiline comments
		//Not yet implemented
		
		// Preprocessor directives (#include, #line etc.)
		colorizeAreaByPattern(CCPP_PREPROCESS_PATTERN, TypeOfText.PREPROCESS_DIRECTIVE);
		
		// Strings
		colorizeAreaByPattern(CCPP_STRING_PATTERN, TypeOfText.STRING);

		//multiline strings
		//Not yet implemented
		
		//Reserved words
		for (int i = 0; i < Math.min(getLimit(), getContent().length); ++i) {
			Matcher m = CCPP_WORD_PATTERN.matcher(getContent()[i]);
		
			while (m.find()) {
				String token = getContent()[i].substring(m.start(), m.end());
			
				colorizeToken(i+1, m.start(), m.end(), token);
			}
		}
	}
	
	private void colorizeToken(int lineNumber, int startOffset, int endOffset, String token) {
		if (CCPP_KEYWORDS.contains(token.toUpperCase(Locale.ROOT))) {			
			getAreas().add(new ColorizingData(lineNumber, startOffset, lineNumber, endOffset, token, TypeOfText.KEYWORD));
		} else {
			if (NumberUtils.isNumber(token)) {
				getAreas().add(new ColorizingData(lineNumber, startOffset, lineNumber, endOffset, token, TypeOfText.CONSTANT));
			}
		}
	}

}
