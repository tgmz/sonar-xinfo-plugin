/*******************************************************************************
  * Copyright (c) 06.07.2021 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.sensors;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;

/**
 * Tokenize files for CPD
 */
public abstract class AbstractCpdSensor implements Sensor {
	private String[] lang;
	private String description;
	protected static final Pattern NON_WHITESPACE = Pattern.compile("\\S+");
	
	/**
	 * Utility class for indexing tokens in one line.
	 */
	protected static final class WordToken {
		public String token;
		public int idx;

		public WordToken(String token, int idx) {
			super();
			this.token = token;
			this.idx = idx;
		}
	}

	public AbstractCpdSensor(String description, String... lang) {
		this.description = description;
		this.lang = lang;
	}

	@Override
	public void describe(SensorDescriptor descriptor) {
		descriptor.name(description);
		descriptor.onlyOnLanguages(lang);
	}

	@Override
	public void execute(SensorContext context) {
		if (context.config().getBoolean(XinfoConfig.XINFO_CPD_OFF).orElse(false)) {
			return;
		}
		
		FilePredicates p = context.fileSystem().predicates();
		
		for (InputFile file : context.fileSystem().inputFiles(p.hasLanguages(lang))) {
			tokenize(file, context);
		}
	}
	
	protected abstract void tokenize(InputFile inputFile, SensorContext context);
	
	/**
	 * Computes non-whitespace tokens inline s, adding an offset.
	 * @param s the line
	 * @param offset the offset to add
	 * @return List of tokes
	 */
	protected List<WordToken> getNonWhitespaceTokens(String s, int offset) {
		List<WordToken> result = new LinkedList<>();
		
		Matcher m = NON_WHITESPACE.matcher(s);
		
		while (m.find()) {
			result.add(new WordToken(m.group(), offset + m.start()));
		}
		
		return result;
	}
}
