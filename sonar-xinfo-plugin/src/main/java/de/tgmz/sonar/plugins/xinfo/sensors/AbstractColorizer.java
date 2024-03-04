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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;

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
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractColorizer.class);
	private static final int DEFAULT_LINES_LIMIT = 5000;
	
	private List<Language> languages;

	protected AbstractColorizer(List<Language> languages) {
		super();
		this.languages = languages;
	}

	@Override
	public void describe(final SensorDescriptor descriptor) {
		StringBuilder s = new StringBuilder();
		List<String> onlyOnLanguages = new LinkedList<>();
		
		languages.forEach(n -> {
			s.append(n.getName() + " "); 
			onlyOnLanguages.add(n.getKey());
		});
		
		
		descriptor.name(s.toString() + "Colorizer Sensor");
		descriptor.onlyOnLanguages(onlyOnLanguages.toArray(new String[languages.size()]));
	}
	
	@Override
	public void execute(final SensorContext context) {
	    FileSystem fs = context.fileSystem();
	    
	    int threshold = context.config().getInt(XinfoConfig.XINFO_LOG_THRESHOLD).orElse(100);
	    
		int ctr = 0;
		
		Collection<String> c = new LinkedList<>();
		
		languages.forEach(n -> c.add(n.getKey()));
		
	    for (InputFile inputFile : fs.inputFiles(fs.predicates().hasLanguages(c))) {
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
		
		int limit = Math.max(DEFAULT_LINES_LIMIT, context.config().getInt(XinfoConfig.COLORIZING_LIMIT).orElse(Integer.valueOf(5000)));
		String charset = context.config().get(XinfoConfig.XINFO_ENCODING).orElse(System.getProperty("file.encoding"));

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
	protected abstract T getColorizing(InputFile f, Charset charset,  int limit) throws IOException;
	
}
