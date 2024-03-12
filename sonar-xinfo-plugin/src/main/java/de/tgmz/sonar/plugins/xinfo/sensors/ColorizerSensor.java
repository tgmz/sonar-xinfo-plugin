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
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;

import de.tgmz.sonar.plugins.xinfo.color.DefaultColorizing;
import de.tgmz.sonar.plugins.xinfo.color.ColorizingData;
import de.tgmz.sonar.plugins.xinfo.color.IColorizing;
import de.tgmz.sonar.plugins.xinfo.color.assembler.AssemblerColorizing;
import de.tgmz.sonar.plugins.xinfo.color.ccpp.CCPPColorizing;
import de.tgmz.sonar.plugins.xinfo.color.cobol.CobolColorizing;
import de.tgmz.sonar.plugins.xinfo.color.pli.PliColorizing;
import de.tgmz.sonar.plugins.xinfo.config.XinfoProjectConfig;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.languages.XinfoLanguage;

/**
 * Abstract sensor to provide syntax highlighting.
 *
 * @param <T> the colorizing scheme to use
 */
public class ColorizerSensor implements Sensor {
	private static final Logger LOGGER = LoggerFactory.getLogger(ColorizerSensor.class);
	private static final int DEFAULT_LINES_LIMIT = 5000;
	
	@Override
	public void describe(final SensorDescriptor descriptor) {
		descriptor.name("XinfoColorizer");
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
		
		int limit = Math.max(DEFAULT_LINES_LIMIT, context.config().getInt(XinfoProjectConfig.COLORIZING_LIMIT).orElse(Integer.valueOf(5000)));
		String charset = context.config().get(XinfoProjectConfig.XINFO_ENCODING).orElse(System.getProperty("file.encoding"));

		IColorizing colorozing = getColorizing(inputFile, Charset.forName(charset), limit);

		for (Iterator<ColorizingData> iterator = colorozing.getAreas().getColorizings().iterator(); iterator.hasNext();) {
			ColorizingData cd = iterator.next();

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
	 * Subclasses must provide the {@link IColorizing} here.
	 * @param f the file to colorize
	 * @param charset the file's encoding
	 * @param limit maximum number of lines to colorize
	 * @return the implementation of the {@link IColorizing}
	 * @throws IOException if the file can't be read 
	 */
	private IColorizing getColorizing(InputFile f, Charset charset,  int limit) throws IOException {
		Language lang = Language.getByFilename(f.filename());
		
		DefaultColorizing ic = null;
		
		switch(lang) {
		case ASSEMBLER:
			ic = new AssemblerColorizing(f, charset, limit);
			break;
		case COBOL:
			ic = new CobolColorizing(f, charset, limit);
			break;
		case C,CPP:
			ic = new CCPPColorizing(f, charset, limit);
			break;
		case PLI:
			ic = new PliColorizing(f, charset, limit);
			break;
		default:
			ic = new DefaultColorizing(f, charset, limit);
		}
		
		ic.createAreas();
		
		return ic;
	}
	
}
