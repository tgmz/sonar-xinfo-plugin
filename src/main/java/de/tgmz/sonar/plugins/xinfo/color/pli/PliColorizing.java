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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

import de.tgmz.sonar.plugins.xinfo.color.AbstractColorizing;
import de.tgmz.sonar.plugins.xinfo.color.ColorizingData;

/**
 * Syntax highlighting for PL/I files.
 */
public class PliColorizing extends AbstractColorizing {
	private static final List<String> PLI_KEYWORDS;
	private static final List<String> PLI_BUILTIN;
	private static final Pattern PLI_COMMENT_PATTERN = Pattern.compile("\\/\\*.*\\*\\/");
	private static final Pattern PLI_PREPROCESS_PATTERN = Pattern.compile("%\\w+");
	private static final Pattern PLI_STRING_PATTERN = Pattern.compile("[\"'].*[\"']");
	private static final Pattern PLI_WORD_PATTERN = Pattern.compile("[\\w$§\\.]+");

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
	
	public PliColorizing(File file, int limit) throws IOException {
		super(file, limit);
	}

	@Override
	protected void createAreas() {
		// Comments
		//TODO: Multiline comments
		colorizeAreaByPattern(PLI_COMMENT_PATTERN, TypeOfText.COMMENT);
		
		// Preprocessor directives (%INCLUDE, %DCL, etc.)
		colorizeAreaByPattern(PLI_PREPROCESS_PATTERN, TypeOfText.PREPROCESS_DIRECTIVE);
		
		// Strings
		//TODO: Multiline strings
		colorizeAreaByPattern(PLI_STRING_PATTERN, TypeOfText.STRING);

		for (int i = 0; i < Math.min(getLimit(), getContent().length); ++i) {
			Matcher m = PLI_WORD_PATTERN.matcher(getContent()[i]);
		
			while (m.find()) {
				String token = getContent()[i].substring(m.start(), m.end());
			
				if (m.start() > 71) {
					continue;
				}
			
				colorizeToken(i+1, m.start(), m.end(), token);
			}
		}
	}
	
	private void colorizeToken(int lineNumber, int startOffset, int endOffset, String token) {
		if (PLI_KEYWORDS.contains(token.toUpperCase(Locale.US))) {			
			getAreas().add(new ColorizingData(lineNumber, startOffset, lineNumber, endOffset, token, TypeOfText.KEYWORD));
		} else {
			if (PLI_BUILTIN.contains(token.toUpperCase(Locale.US))) {			
				getAreas().add(new ColorizingData(lineNumber, startOffset, lineNumber, endOffset, token, TypeOfText.KEYWORD_LIGHT));
			} else {
				if (NumberUtils.isNumber(token)) {
					getAreas().add(new ColorizingData(lineNumber, startOffset, lineNumber, endOffset, token, TypeOfText.CONSTANT));
				}
			}
		}
	}

}
