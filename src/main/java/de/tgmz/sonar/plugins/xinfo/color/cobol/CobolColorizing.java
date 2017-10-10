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
package de.tgmz.sonar.plugins.xinfo.color.cobol;

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
 * Syntax highlighting for Cobol files.
 */
public class CobolColorizing extends AbstractColorizing {
	private static final List<String> KEYWORDS;
	private static final Pattern COBOL_WORD_PATTERN = Pattern.compile("[\\w$§\\.\\-]+");
	private static final Pattern COBOL_STRING_PATTERN = Pattern.compile("[\"'].*[\"']");
	
	static {
		// Keywords
		KEYWORDS = new ArrayList<>(ReservedWords.values().length);
		
		for (ReservedWords rw : ReservedWords.values()) {
			KEYWORDS.add(rw.toString());
		}
	}

	public CobolColorizing(File file, int limit) throws IOException {
		super(file, limit);
	}

	@Override
	protected void createAreas() {
		// Comments
		colorizeComments();
		
		// Strings
		//TODO: Multiline strings
		colorizeAreaByPattern(COBOL_STRING_PATTERN, TypeOfText.STRING);

		for (int i = 0; i < Math.min(getLimit(), getContent().length); ++i) {
			// Split the text by word characters and highlight keywords and numeric constants
			Matcher m = COBOL_WORD_PATTERN.matcher(getContent()[i]);
		
			while (m.find()) {
				String token = getContent()[i].substring(m.start(), m.end());
				
				if (m.end() < 7 || m.start() > 71) {
					continue;
				}
			
				colorizeToken(i+1, m.start(), m.end(), token);
			}
		}
	}
	
	private void colorizeToken(int lineNumber, int startOffset, int endOffset, String token) {
		if (KEYWORDS.contains(token.toUpperCase(Locale.US))) {			
			getAreas().add(new ColorizingData(lineNumber, startOffset, lineNumber, endOffset, token, TypeOfText.KEYWORD));
		} else {
			if (NumberUtils.isNumber(token)) {
				getAreas().add(new ColorizingData(lineNumber, startOffset, lineNumber, endOffset, token, TypeOfText.CONSTANT));
			}
		}
	}
	
	private void colorizeComments() {
		Pattern p0 = Pattern.compile("^.{6}\\*.*$");
		
		for (int i = 0; i < getContent().length; ++i) {
			Matcher m = p0.matcher(getContent()[i]);
		
			while (m.find()) {
				String s = getContent()[i].substring(m.start(), m.end());
			
				getAreas().add(new ColorizingData(i+1, m.start(), i+1, m.end(), s, TypeOfText.COMMENT));
			}
		}
	}
}
