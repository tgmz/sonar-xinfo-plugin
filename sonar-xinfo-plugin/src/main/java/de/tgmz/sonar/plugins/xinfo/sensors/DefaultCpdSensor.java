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

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.cpd.NewCpdTokens;

import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Tokenize files for CPD
 */
public class DefaultCpdSensor extends AbstractCpdSensor {
	public DefaultCpdSensor() {
		super("XINFO default CPD Sensor", Language.PLI.getKey(), Language.ASSEMBLER.getKey(), Language.C.getKey(), Language.CPP.getKey());
	}
	
	protected void tokenize(InputFile inputFile, SensorContext context) {
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
}
