/*******************************************************************************
  * Copyright (c) 21.03.2020 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.sensors;

import java.nio.charset.Charset;
import java.util.Iterator;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;
import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Sensor for QA of SAS programs.
 */
public class MacroIssuesLoader extends AbstractXinfoIssuesLoader {
	private static final Logger LOGGER = Loggers.get(MacroIssuesLoader.class);

	public MacroIssuesLoader(final FileSystem fileSystem) {
		super(fileSystem, Language.MACRO);
	}
	
	@Override
	public void execute(SensorContext aContext) {
		LOGGER.debug("execute");

		this.context = aContext;
		
		Charset charset = Charset.forName(context.config().get(XinfoConfig.XINFO_ENCODING).orElse(Charset.defaultCharset().name()));
		
		Iterator<InputFile> fileIterator = fileSystem.inputFiles(fileSystem.predicates().hasLanguage(Language.MACRO.getKey())).iterator();

		int ctr = 0;
		
		while (fileIterator.hasNext()) {
			InputFile inputFile = fileIterator.next();
			
			findMc(inputFile, charset);
			
			if (++ctr % 100 == 0) {
				LOGGER.info("{} files processed, current is {}", ctr, inputFile.filename());
			}
		}
	}
}
