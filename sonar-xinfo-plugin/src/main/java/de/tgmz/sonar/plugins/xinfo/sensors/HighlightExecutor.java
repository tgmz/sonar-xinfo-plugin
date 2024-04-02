/*******************************************************************************
  * Copyright (c) 21.03.2024 Thomas Zierer.
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
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.SensorContext;
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

public class HighlightExecutor implements Callable<NewHighlighting> {
	private static final Logger LOGGER = LoggerFactory.getLogger(HighlightExecutor.class);
	private static final int DEFAULT_LINES_LIMIT = 5000;
	private SensorContext context;
	private InputFile inputFile;
	private int limit;
	private String charset;
	
	public HighlightExecutor(SensorContext context, InputFile inputFile) {
		super();
		this.context = context;
		this.inputFile = inputFile;
		
		limit = Math.max(DEFAULT_LINES_LIMIT, context.config().getInt(XinfoProjectConfig.COLORING_LIMIT).orElse(Integer.valueOf(5000)));
		charset = context.config().get(XinfoProjectConfig.XINFO_ENCODING).orElse(System.getProperty("file.encoding"));
	}
	
	@Override
	public NewHighlighting call() throws IOException {
		NewHighlighting nhl = context.newHighlighting().onFile(inputFile);
		
		IColoring coloring = getColoring(inputFile, Charset.forName(charset), limit);

		for (ColoringData cd : coloring.getAreas().getColorings()) {
			TextRange newRange;
			try {
				newRange = inputFile.newRange(cd.getStartLineNumber(), cd.getStartOffset(), cd.getEndLineNumber(), cd.getEndOffset());
			} catch (IllegalArgumentException e) {
				LOGGER.error("Invalid text range {}", cd);
				
				continue;
			}
		
			nhl.highlight(newRange, cd.getType());
		}

		return nhl;
	}
	private IColoring getColoring(InputFile f, Charset charset,  int limit) throws IOException {
		Language lang = Language.getByFilename(f.filename());
		
		switch(lang) {
		case ASSEMBLER, MACRO:
			return new AssemblerColoring(f, charset, limit);
		case COBOL, COPYBOOK:
			return new CobolColoring(f, charset, limit);
		case C,CPP:
			return new CCPPColoring(f, charset, limit);
		case PLI, INCLUDE:
			return new PliColoring(f, charset, limit);
		default:
			return new DefaultColoring(f, charset, limit);
		}
	}
}
