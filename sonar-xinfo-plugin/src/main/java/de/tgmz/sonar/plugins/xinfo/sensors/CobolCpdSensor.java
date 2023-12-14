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

import org.apache.commons.io.IOUtils;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.cpd.NewCpdTokens;

import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Tokenize files for CPD
 */
public class CobolCpdSensor extends AbstractCpdSensor {
	public CobolCpdSensor() {
		super("XINFO COBOL CPD Sensor", Language.COBOL.getKey());
	}
	
	protected void tokenize(InputFile inputFile, SensorContext context) {
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
}
