/*******************************************************************************
  * Copyright (c) 09.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Configuration;

import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;

/**
 * Provides program informations by walking through the file system. 
 * Searches the file system for a suitable xinfo.xml|events file and parses it.
 */
public class XinfoFileProvider extends AbstractXinfoProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(XinfoFileProvider.class);
	
	public XinfoFileProvider(Configuration configuration) {
		super(configuration);
	}

	@Override
	public PACKAGE getXinfo(InputFile pgm) throws XinfoException {
		PACKAGE result = new de.tgmz.sonar.plugins.xinfo.generated.plicomp.ObjectFactory().createPACKAGE();

		String compOutput = FilenameUtils.removeExtension(pgm.filename());
		
		String compOutputRoot = getConfiguration().get(XinfoConfig.XINFO_ROOT).orElse("xinfo");
		
		Path p = Paths.get(compOutputRoot == null ? "" : compOutputRoot).toAbsolutePath();
		
		Collection<File> listFiles = FileUtils.listFiles(p.toFile(), new WildcardFileFilter(compOutput + ".*", IOCase.SYSTEM), TrueFileFilter.TRUE);
		
		switch (listFiles.size()) {
		case 0:
			LOGGER.error("Cannot find compiler output for {}", pgm);
			break;
		case 1:
			File next = listFiles.iterator().next();
			
			try (InputStream is = new FileInputStream(next)) {
				if ("xml".equals(FilenameUtils.getExtension(next.getName()))) {
					result = createXinfo(is);
				} else {
					result = createXinfoFromEvent(is);
				}
			} catch (IOException e) {
				LOGGER.error("Exception parsing {}", compOutput, e);
			}
			break;
		default:
			LOGGER.error("Found multiple files {}", compOutput);
			break;
		}
		
		return result;
	}
}
