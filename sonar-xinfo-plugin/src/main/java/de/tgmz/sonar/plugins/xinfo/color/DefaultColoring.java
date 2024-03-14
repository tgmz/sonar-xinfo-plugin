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
package de.tgmz.sonar.plugins.xinfo.color;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

/**
 * Common functions for Syntax highlighting.
 */
public class DefaultColoring implements IColoring {
    public static final ThreadLocal<NumberFormat> NF = ThreadLocal.withInitial(NumberFormat::getNumberInstance);

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultColoring.class);

	private static final Pattern DEFAULT_WORD_PATTERN = Pattern.compile("\\w+");
	private static final Pattern DEFAULT_STRING_PATTERN = Pattern.compile("[\"'].*[\"']");

	/** The areas to color. */
	private HighligthedAreas areas;
	private String[] content;
	private int limit;
	
	/**
	 * Creates the areas of a file to color.
	 * @param file the file to color
	 * @param charset the files encoding
	 * @param limit maximum number of lines to color
	 * @throws IOException if the file can't be read
	 */
    public DefaultColoring(InputFile file, Charset charset, int limit) throws IOException {
		this.limit = limit;
		
		LOGGER.debug("color file {}", file);
		
		List<String> readLines = IOUtils.readLines(file.inputStream(), charset);
		
		if (readLines.size() > limit && LOGGER.isInfoEnabled()) {
			LOGGER.info("File {} containes {} lines. Syntax highlighting will be limited to {} lines"
					, file.filename(), NF.get().format(readLines.size()), NF.get().format(this.limit));
		}
		
		content = new String[Math.min(readLines.size(), limit)];
		
		System.arraycopy(readLines.toArray(new String[readLines.size()]), 0, content, 0, content.length);
	}

	public HighligthedAreas getAreas() {
		if (areas == null) {
			areas = new HighligthedAreas();
			
			createAreas();
		}
		
		return areas;
	}

	/**
	 * Every subclass should override this.
	 */
	public void createAreas() {
		// Strings
		colorAreaByPattern(DEFAULT_STRING_PATTERN, TypeOfText.STRING);
		
		colorTokens(DEFAULT_WORD_PATTERN,  Collections.emptyMap(), -1, Integer.MAX_VALUE);
	}
	
	/**
	 * colors every area of the file's content that matches a regex in a manner described by typeOfText
	 * @param p the regex
	 * @param typeOfText the type
	 */
	protected void colorAreaByPattern(Pattern p, TypeOfText typeOfText) {
		for (int i = 0; i < content.length; i++) {
			Matcher m = p.matcher(content[i]);
			
			while (m.find()) {
				String s = content[i].substring(m.start(), m.end());
				
				areas.add(new ColoringData(i+1, m.start(), i+1, m.end(), s, typeOfText));
			}
		}
	}
	
	protected void colorTokens(Pattern pattern, Map<TypeOfText, List<String>> colorTokens, int left, int right) {
		for (int i = 0; i < Math.min(limit, getContent().length); ++i) {
			Matcher m = pattern.matcher(getContent()[i]);
	
			while (m.find()) {
				String token = getContent()[i].substring(m.start(), m.end());
		
				if (m.end() <= left || m.start() >= right) {
					continue;
				}
				
				colorToken(colorTokens, i+1, m.start(), m.end(), token);
			}
		}
	}
	
	private void colorToken(Map<TypeOfText, List<String>> colorTokens, int lineNumber, int startOffset, int endOffset, String token) {
		if (NumberUtils.isNumber(token)) {
			getAreas().add(new ColoringData(lineNumber, startOffset, lineNumber, endOffset, token, TypeOfText.CONSTANT));
		} else {
			for (Entry<TypeOfText, List<String>> entry : colorTokens.entrySet()) {
				if (entry.getValue().contains(token.toUpperCase(Locale.ROOT))) {
					getAreas().add(new ColoringData(lineNumber, startOffset, lineNumber, endOffset, token, entry.getKey()));
				
					break;
				}
			}
		}
	}

	protected String[] getContent() {
		return content;
	}

	protected static ThreadLocal<NumberFormat> getNf() {
		return NF;
	}

	protected static Logger getLogger() {
		return LOGGER;
	}
}
