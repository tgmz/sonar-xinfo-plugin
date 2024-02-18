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
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.FILE;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.MESSAGE;
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
	public PACKAGE getXinfo(InputFile pgm) throws XinfoException {
		PACKAGE result = new de.tgmz.sonar.plugins.xinfo.generated.plicomp.ObjectFactory().createPACKAGE();

		String compOutput = FilenameUtils.removeExtension(pgm.filename());
		
		String compOutputRoot = getConfiguration().get(XinfoConfig.XINFO_ROOT).orElse("xml");
		
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
					result = getXinfoFromEvent(is);
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
	private PACKAGE getXinfoFromEvent(InputStream is) throws IOException {
		de.tgmz.sonar.plugins.xinfo.generated.plicomp.ObjectFactory of = new de.tgmz.sonar.plugins.xinfo.generated.plicomp.ObjectFactory();
		
		PACKAGE ccomp = of.createPACKAGE();
		ccomp.setFILEREFERENCETABLE(of.createFILEREFERENCETABLE());

		List<String> readLines = IOUtils.readLines(is, Charset.defaultCharset());
		
		readLines.forEach(line -> {
			// See https://www-40.ibm.com/servers/resourcelink/svc00100.nsf/pages/zOSV2R3sc147307/$file/cbcux01_v2r3.pdf
			// page 648 for the detailed format of the SYSEVENT file
			if (line.startsWith("ERROR")) {
				// The ERROR field looks like this:
				// ERROR 0 1 0 0 3 3 0 0 CCNnnnn E 12 26 Undeclared identifier add.
				//       | | | | | | | | |       | |  |  |
				//       A B C D E F G H I       J K  L  M
				String[] s = line.split("\\s", 14);
				
				MESSAGE m = of.createMESSAGE();

				m.setMSGFILE(s[2]);						// B: Increments starting with 1 for the primary file
				m.setMSGLINE(s[5]);						// E: The source line number for which the message was issued.
				m.setMSGNUMBER(s[9] + s[10]);			// I: String Containing the message identifier
														// J: Message severity character (I/W/E/S/U) (processed in AbstractXinfoIssuesLoader)
				m.setMSGTEXT(s[13]);					// M: String containing message text
				
				ccomp.getMESSAGE().add(m);
			}
			if (line.startsWith("FILEID")) {
				// The FILEID field looks like this:
				// FILEID 0 1 0 10 ./simple.c
				//        | | | |  |
				//        A B C D  E
				String[] s = line.split("\\s", 6);
				
				FILE f = of.createFILE();
				f.setFILENUMBER(s[2]);					// B: Increments starting with 1 for the primary file
				f.setINCLUDEDONLINE(s[3]);				// C: The line number of the #include directive. For the primary source file this value is 0
				f.setFILENAME(s[5])	;					// E: String containing file/dataset name.
				
				ccomp.getFILEREFERENCETABLE().getFILE().add(f);
			}
		});
		
		ccomp.getFILEREFERENCETABLE().setFILECOUNT(String.valueOf(ccomp.getFILEREFERENCETABLE().getFILE().size()));
		
		return ccomp;
		
	}
}
