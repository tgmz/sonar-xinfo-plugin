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
package de.tgmz.sonar.plugins.xinfo.color.macro;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

import de.tgmz.sonar.plugins.xinfo.color.AbstractColorizing;
import de.tgmz.sonar.plugins.xinfo.color.ColorizingData;

/**
 * Syntax highlighting for Cobol files.
 */
public class MacroColorizing extends AbstractColorizing {
	private static final Pattern MACRO_WORD_PATTERN = Pattern.compile("[\\w$§\\.\\-]+");
	private static final Pattern MACRO_STRING_PATTERN = Pattern.compile("[\"'].*[\"']");
	
	public MacroColorizing(InputFile file, Charset charset, int limit) throws IOException {
		super(file, charset, limit);
	}

	@Override
	protected void createAreas() {
		// Strings
		//TODO: Multiline strings
		colorizeAreaByPattern(MACRO_STRING_PATTERN, TypeOfText.STRING);

		for (int i = 0; i < Math.min(getLimit(), getContent().length); ++i) {
			// Split the text by word characters and highlight keywords and numeric constants
			Matcher m = MACRO_WORD_PATTERN.matcher(getContent()[i]);
		
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
		if (NumberUtils.isNumber(token)) {
			getAreas().add(new ColorizingData(lineNumber, startOffset, lineNumber, endOffset, token, TypeOfText.CONSTANT));
		}
	}
}
