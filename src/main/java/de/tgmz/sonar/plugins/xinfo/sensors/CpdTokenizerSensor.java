/*******************************************************************************
  * Copyright (c) 16.09.2017 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.sensors;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.cpd.NewCpdTokens;

import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Tokenize files for CPD
 */
public class CpdTokenizerSensor implements Sensor {
	// All languages are processed equaly
	private static final String[] LANG = new String[] {Language.PLI.getKey(), 
														Language.COBOL.getKey(),
														Language.ASSEMBLER.getKey(),
														};
	private static final Pattern NON_WHITESPACE = Pattern.compile("\\S+");

	@Override
	public void describe(SensorDescriptor descriptor) {
		descriptor.name("XINFO CPD Sensor");
		descriptor.onlyOnLanguages(LANG);
	}

	@Override
	public void execute(SensorContext context) {
		FilePredicates p = context.fileSystem().predicates();
		
		for (InputFile file : context.fileSystem().inputFiles(p.hasLanguages(LANG))) {
			tokenize(file, context);
		}
	}
	
	private void tokenize(InputFile inputFile, SensorContext context) {
		// Column 0 is for carriage control characters, colums > 71 for comments
		// For COBOL colums 1-7 are comments
		int left = Language.COBOL.getKey().equals(inputFile.language()) ? 7 : 1;
 
		int lineIdx = 1;
		
		NewCpdTokens cpdTokens = context.newCpdTokens().onFile(inputFile);
		
		try {
			for (String line : IOUtils.readLines(inputFile.inputStream(), inputFile.charset())) {
				if (line.length() > left) {
					String code = line.substring(left, Math.min(line.length(), 72));
					
					Matcher m = NON_WHITESPACE.matcher(code);
				
					while (m.find()) {
						cpdTokens.addToken(inputFile.newRange(lineIdx, left + m.start(), lineIdx, left + m.end()), m.group());
					}
				}
				
				lineIdx++;
			}
		} catch (IOException e) {
			throw new IllegalStateException("Unable to tokenize", e);
		}
		
		cpdTokens.save();
	}
}
