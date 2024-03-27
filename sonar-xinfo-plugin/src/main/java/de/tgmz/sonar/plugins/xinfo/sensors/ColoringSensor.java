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
package de.tgmz.sonar.plugins.xinfo.sensors;

import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;

import de.tgmz.sonar.plugins.xinfo.color.ColoringData;
import de.tgmz.sonar.plugins.xinfo.color.DefaultColoring;
import de.tgmz.sonar.plugins.xinfo.color.IColoring;
import de.tgmz.sonar.plugins.xinfo.color.assembler.AssemblerColoring;
import de.tgmz.sonar.plugins.xinfo.color.ccpp.CCPPColoring;
import de.tgmz.sonar.plugins.xinfo.color.cobol.CobolColoring;
import de.tgmz.sonar.plugins.xinfo.color.pli.PliColoring;
import de.tgmz.sonar.plugins.xinfo.config.XinfoProjectConfig;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.languages.XinfoLanguage;

/**
 * Abstract sensor to provide syntax highlighting.
 *
 * @param <T> the coloring scheme to use
 */
public class ColoringSensor implements Sensor {
	private static final Logger LOGGER = LoggerFactory.getLogger(ColoringSensor.class);
	private static final int DEFAULT_LINES_LIMIT = 5000;
	
	@Override
	public void describe(final SensorDescriptor descriptor) {
		descriptor.name("XinfoColor");
		descriptor.onlyOnLanguages(XinfoLanguage.KEY);
	}
	
	@Override
	public void execute(final SensorContext context) {
	    int threshold = context.config().getInt(XinfoProjectConfig.XINFO_LOG_THRESHOLD).orElse(100);

		FilePredicates p = context.fileSystem().predicates();
		
		int ctr = 0;
		
		for (InputFile inputFile : context.fileSystem().inputFiles(p.hasLanguages(XinfoLanguage.KEY))) {
			try {
				highlightFile(inputFile, context);
				
				if (++ctr % threshold == 0) {
					LOGGER.info("{} files processed, current is {}", ctr, inputFile);
				}
				
			} catch (IOException e) {
				LOGGER.error("Error creating highlighting on file " + inputFile, e);
			}
		}
	}

	private void highlightFile(final InputFile inputFile, final SensorContext context) throws IOException {
		NewHighlighting newHighlighting = context.newHighlighting().onFile(inputFile);
		
		int limit = Math.max(DEFAULT_LINES_LIMIT, context.config().getInt(XinfoProjectConfig.COLORING_LIMIT).orElse(Integer.valueOf(5000)));
		String charset = context.config().get(XinfoProjectConfig.XINFO_ENCODING).orElse(System.getProperty("file.encoding"));

		IColoring coloring = getColoring(inputFile, Charset.forName(charset), limit);

		for (ColoringData cd : coloring.getAreas().getColorings()) {
			TextRange newRange;
			try {
				newRange = inputFile.newRange(cd.getStartLineNumber(), cd.getStartOffset(), cd.getEndLineNumber(), cd.getEndOffset());
			} catch (IllegalArgumentException e) {
				LOGGER.error("Invalid text range {}", cd);
				
				continue;
			}
		
			newHighlighting.highlight(newRange, cd.getType());
		}

		newHighlighting.save();
	}
	
	/**
	 * Subclasses must provide the {@link IColoring} here.
	 * @param f the file to color
	 * @param charset the file's encoding
	 * @param limit maximum number of lines to color
	 * @return the implementation of the {@link IColoring}
	 * @throws IOException if the file can't be read 
	 */
	private IColoring getColoring(InputFile f, Charset charset,  int limit) throws IOException {
		Language lang = Language.getByFilename(f.filename());
		
		IColoring ic;
		
		switch(lang) {
		case ASSEMBLER, MACRO:
			ic = new AssemblerColoring(f, charset, limit);
			break;
		case COBOL, COPYBOOK:
			ic = new CobolColoring(f, charset, limit);
			break;
		case C,CPP:
			ic = new CCPPColoring(f, charset, limit);
			break;
		case PLI, INCLUDE:
			ic = new PliColoring(f, charset, limit);
			break;
		default:
			ic = new DefaultColoring(f, charset, limit);
		}
		
		return ic;
	}
	
}
