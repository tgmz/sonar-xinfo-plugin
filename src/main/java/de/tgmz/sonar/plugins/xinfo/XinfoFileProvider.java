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
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;

/**
 * Provides programinformations by walking through the filesystem. 
 * Searches the filesystem for a suitable xinfo.xml file and parses it.
 */
public class XinfoFileProvider extends AbstractXinfoProvider {
	private static final Logger LOGGER = Loggers.get(XinfoFileProvider.class);
	
	public XinfoFileProvider(Configuration configuration) {
		super(configuration);
	}

	@Override
	public PACKAGE getXinfo(IXinfoAnalyzable pgm) throws XinfoException {
		PACKAGE result = new de.tgmz.sonar.plugins.xinfo.generated.plicomp.ObjectFactory().createPACKAGE();
		
		String xinfoFile;
		
		xinfoFile = pgm.getName() + ".xml";
				
		String xinfoRoot = getConfiguration().get(XinfoConfig.XINFO_ROOT).orElse("xml");
		
		Path p = Paths.get(xinfoRoot == null ? "" : xinfoRoot).toAbsolutePath();
		
		Collection<File> listFiles = FileUtils.listFiles(p.toFile(), new NameFileFilter(xinfoFile, IOCase.SYSTEM), TrueFileFilter.TRUE);
		
		switch (listFiles.size()) {
		case 0:
			LOGGER.error("Cannot find file " + xinfoFile);
			break;
		case 1:
			try (InputStream is = new FileInputStream(listFiles.iterator().next())) {
				result = createXinfo(is);
			} catch (IOException e) {
				LOGGER.error("Exception parsing " + xinfoFile, e);
			}
			break;
		default:
			LOGGER.error("Found multiple files " + xinfoFile);
			break;
		}
		
		return result;
	}
}
