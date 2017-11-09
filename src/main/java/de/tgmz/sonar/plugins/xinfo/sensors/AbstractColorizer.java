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
import java.util.Iterator;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import de.tgmz.sonar.plugins.xinfo.color.ColorizingData;
import de.tgmz.sonar.plugins.xinfo.color.IColorizing;
import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;
import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Abstract sensor to provide syntax highlighting.
 *
 * @param <T> the colorizing scheme to use
 */
public abstract class AbstractColorizer<T extends IColorizing> implements Sensor {
	private static final Logger LOGGER = Loggers.get(AbstractColorizer.class);
	private static final int DEFAULT_LIMIT = 5000;
	private Language lang;

	public AbstractColorizer(Language lang) {
		super();
		this.lang = lang;
	}

	@Override
	public void describe(final SensorDescriptor descriptor) {
		descriptor.name(lang.getName() + " Colorizer Sensor");
		descriptor.onlyOnLanguage(lang.getKey());
	}
	
	@Override
	public void execute(final SensorContext context) {
		LOGGER.debug("execute");

	    FileSystem fs = context.fileSystem();
	    
		int ctr = 0;
		
	    for (InputFile inputFile : fs.inputFiles(fs.predicates().hasLanguage(lang.getKey()))) {
			NewHighlighting newHighlighting = context.newHighlighting().onFile(inputFile);
			
			try {
				IColorizing ph = getColorizing(inputFile, Math.max(DEFAULT_LIMIT, context.config().getInt(XinfoConfig.COLORIZING_LIMIT).orElse(Integer.valueOf(5000))));
	
				for (Iterator<ColorizingData> iterator = ph.getAreas().iterator(); iterator.hasNext();) {
					ColorizingData hd = iterator.next();

					//CHECKSTYLE DISABLE LineLength for 1 line
					TextRange newRange = ((DefaultInputFile) inputFile).newRange(hd.getStartLineNumber(), hd.getStartOffset(), hd.getEndLineNumber(), hd.getEndOffset());
				
					newHighlighting.highlight(newRange, hd.getType());
				}
	
				if (++ctr % 100 == 0) {
					LOGGER.info("{} files processed, current is {}", ctr, inputFile.filename());
				}
				
				newHighlighting.save();
			} catch (IOException e) {
				LOGGER.error("Error creating highlighting on file " + inputFile.filename(), e);
			}
		}
	}
	
	/**
	 * Subclasses must provide the {@link IColorizing} here.
	 * @param f the file to colorize
	 * @param limit maximum number of lines to colorize
	 * @return the implementation of the {@link IColorizing}
	 * @throws IOException if the file can't be read 
	 */
	protected abstract T getColorizing(InputFile f, int limit) throws IOException;
}
