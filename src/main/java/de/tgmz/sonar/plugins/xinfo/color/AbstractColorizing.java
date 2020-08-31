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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * Common functions for Syntax highlighting.
 */
public abstract class AbstractColorizing implements IColorizing {
	private static final Logger LOGGER = Loggers.get(AbstractColorizing.class);

	/** The areas to colorize. */
	private HighligthedAreas areas;
	private String[] content;
	private int limit;
	
    public static final ThreadLocal<NumberFormat> NF = ThreadLocal.withInitial(() -> NumberFormat.getNumberInstance());

	/**
	 * Creates the areas of a file to color.
	 * @param file the file to color
	 * @param charset the files encoding
	 * @param limit maximum number of lines to colorize
	 * @throws IOException if the file can't be read
	 */
	public AbstractColorizing(InputFile file, Charset charset, int limit) throws IOException {
		this.limit = limit;
		
		LOGGER.debug("Colorize file {}", file.toString());
		
		List<String> readLines = IOUtils.readLines(file.inputStream(), charset);
		
		if (readLines.size() > limit) {
			LOGGER.warn("File {} containes {} lines. Syntax highlighting will be limited to {} lines"
					, file.filename(), NF.get().format(readLines.size()), NF.get().format(this.limit));
		}
		
		content = new String[Math.min(readLines.size(), limit)];
		
		System.arraycopy(readLines.toArray(new String[readLines.size()]), 0, content, 0, content.length);
		
		areas = new HighligthedAreas();
		
		createAreas();
	}

	public HighligthedAreas getAreas() {
		return areas;
	}

	/**
	 * Every subclass must implement this.
	 */
	protected abstract void createAreas();
	
	/**
	 * Colorizes every area of the file's content that matches a regex in a manner described by typeOfText
	 * @param p the regex
	 * @param typeOfText the type
	 */
	protected void colorizeAreaByPattern(Pattern p, TypeOfText typeOfText) {
		for (int i = 0; i < content.length; i++) {
			Matcher m = p.matcher(content[i]);
			
			while (m.find()) {
				String s = content[i].substring(m.start(), m.end());
				
				areas.add(new ColorizingData(i+1, m.start(), i+1, m.end(), s, typeOfText));
			}
		}
	}

	protected String[] getContent() {
		return content;
	}

	protected int getLimit() {
		return limit;
	}
}
