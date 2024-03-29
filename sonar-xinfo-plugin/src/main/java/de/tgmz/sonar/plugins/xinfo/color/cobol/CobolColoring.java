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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

import de.tgmz.sonar.plugins.xinfo.color.DefaultColoring;
import de.tgmz.sonar.plugins.xinfo.color.ColoringData;

/**
 * Syntax highlighting for Cobol files.
 */
public class CobolColoring extends DefaultColoring {
	private static final List<String> KEYWORDS;
	private static final Pattern COBOL_WORD_PATTERN = Pattern.compile("[\\w$§\\.\\-]+");
	private static final Pattern COBOL_STRING_PATTERN = Pattern.compile("[\"'].*[\"']");
	private static final Pattern COBOL_COMMENT_PATTERN = Pattern.compile("^.{6}\\*.*$");
	
	static {
		// Keywords
		KEYWORDS = new ArrayList<>(ReservedWords.values().length);
		
		for (ReservedWords rw : ReservedWords.values()) {
			KEYWORDS.add(rw.toString());
		}
	}

	public CobolColoring(InputFile file, Charset charset, int limit) throws IOException {
		super(file, charset, limit);
	}

	@Override
	public void createAreas() {
		// Comments
		colorComments();
		
		// Strings
		colorAreaByPattern(COBOL_STRING_PATTERN, TypeOfText.STRING);

		//Multiline strings
		//Not yet implemented
		
		//Reserved words
		colorTokens(COBOL_WORD_PATTERN, Collections.singletonMap(TypeOfText.KEYWORD, KEYWORDS), 7, 71);
	}
	
	private void colorComments() {
		for (int i = 0; i < getContent().length; ++i) {
			Matcher m = COBOL_COMMENT_PATTERN.matcher(getContent()[i]);
		
			while (m.find()) {
				String s = getContent()[i].substring(m.start(), m.end());
			
				getAreas().add(new ColoringData(i+1, m.start(), i+1, m.end(), s, TypeOfText.COMMENT));
			}
		}
	}
}
