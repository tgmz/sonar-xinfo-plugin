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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.cpd.NewCpdTokens;

import de.tgmz.sonar.plugins.xinfo.config.XinfoProjectConfig;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.languages.XinfoLanguage;

/**
 * Tokenize files for CPD
 */
public class XinfoCpdSensor implements Sensor {
	protected static final Pattern NON_WHITESPACE = Pattern.compile("\\S+");
	
	/**
	 * Utility class for indexing tokens in one line.
	 */
	protected static final class WordToken {
		String token;
		int idx;

		public WordToken(String token, int idx) {
			super();
			this.token = token;
			this.idx = idx;
		}
	}

	@Override
	public void describe(SensorDescriptor descriptor) {
		descriptor.name("XinfoCpdSensor");
		descriptor.onlyOnLanguages(XinfoLanguage.KEY);
	}

	@Override
	public void execute(SensorContext context) {
		if (context.config().getBoolean(XinfoProjectConfig.XINFO_CPD_OFF).orElse(false)) {
			return;
		}
		
		FilePredicates p = context.fileSystem().predicates();
		
		for (InputFile inputFile : context.fileSystem().inputFiles(p.hasLanguages(XinfoLanguage.KEY))) {
			if (Language.getByFilename(inputFile.filename()) == Language.COBOL) {
				tokenizeCobol(inputFile, context);
			} else {
				tokenize(inputFile, context);
			}
		}
	}
	
	private void tokenizeCobol(InputFile inputFile, SensorContext context) {
		int lineIdx = 1;
		
		NewCpdTokens cpdTokens = context.newCpdTokens().onFile(inputFile);
		
		try {
			for (String line : IOUtils.readLines(inputFile.inputStream(), inputFile.charset())) {
				if (line.length() > 7 && line.charAt(6) != '*') {
					String code = line.substring(7, Math.min(line.length(), 72));
					
					for (WordToken wt : getNonWhitespaceTokens(code, 7)) {
						cpdTokens.addToken(inputFile.newRange(lineIdx, wt.idx, lineIdx, wt.idx + wt.token.length()), wt.token);
					}
				}
				
				lineIdx++;
			}
		} catch (IOException e) {
			throw new IllegalStateException("Unable to tokenize", e);
		}
		
		cpdTokens.save();
	}
	private void tokenize(InputFile inputFile, SensorContext context) {
		// Column 0 is for carriage control characters, columns > 71 for comments
		int lineIdx = 1;
		
		NewCpdTokens cpdTokens = context.newCpdTokens().onFile(inputFile);
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputFile.inputStream(), inputFile.charset()))) {
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		    	if (line.length() > 1) {
		    		String code = line.substring(1, Math.min(line.length(), 72));
		    	
		    		cpdTokens.addToken(inputFile.newRange(lineIdx, 1, lineIdx, code.length() + 1), code);
		    	}
		    	
		    	lineIdx++;
		    }
		} catch (IOException e) {
			throw new IllegalStateException("Unable to tokenize", e);
		}
		
		cpdTokens.save();
	}
	
	/**
	 * Computes non-whitespace tokens inline s, adding an offset.
	 * @param s the line
	 * @param offset the offset to add
	 * @return List of tokes
	 */
	private List<WordToken> getNonWhitespaceTokens(String s, int offset) {
		List<WordToken> result = new LinkedList<>();
		
		Matcher m = NON_WHITESPACE.matcher(s);
		
		while (m.find()) {
			result.add(new WordToken(m.group(), offset + m.start()));
		}
		
		return result;
	}
}
