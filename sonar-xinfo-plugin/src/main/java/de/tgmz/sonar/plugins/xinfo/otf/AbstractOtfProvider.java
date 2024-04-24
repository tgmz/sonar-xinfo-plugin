/*******************************************************************************
  * Copyright (c) 03.03.2024 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.otf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Configuration;

import de.tgmz.sonar.plugins.xinfo.AbstractXinfoProvider;
import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.config.XinfoFtpConfig;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;
import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Loads issues "on-the-fly", i.e. with no stored XINFO files by invoking the appropriate compiler. 
 */
public abstract class AbstractOtfProvider extends AbstractXinfoProvider {
	protected AbstractOtfProvider(Configuration configuration) {
		super(configuration);
	}

	protected PACKAGE createXinfo(InputFile pgm, byte[] xinfo) throws IOException, XinfoException {
		try (InputStream is = new ByteArrayInputStream(xinfo)) {
			Language lang = Language.getByFilename(pgm.filename());
			
			return (lang == Language.C || lang == Language.CPP) ? super.createXinfoFromEvent(is) : super.createXinfo(is);
		}
	}

	protected String createJcl(InputFile inputFile, String sysxmlsd) throws IOException, XinfoException {
		String template = null;

		switch (Language.getByFilename(inputFile.filename())) {
		case COBOL:
			template = "elaxfcoc.txt";
			break;
		case C:
			template = "elaxfcpc.txt";
			break;
		case CPP:
			template = "elaxfcpp.txt";
			break;
		case ASSEMBLER:
			template = "elaxfasm.txt";
			break;
		case PLI:
		default:
			template = "elaxfpl1.txt";
			break;
		}
		
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(template);
				Reader r = new InputStreamReader(is, StandardCharsets.UTF_8)) {
				String s = IOUtils.toString(r);
			
				Stream<String> lines = MessageFormat.format(s
						, getConfiguration().get(XinfoFtpConfig.XINFO_OTF_JOBCARD).orElseThrow()
						, FilenameUtils.removeExtension(inputFile.filename()).toUpperCase(Locale.getDefault())
						, inputFile.contents()
						, sysxmlsd
						, getConfiguration().get(XinfoFtpConfig.XINFO_OTF_SYSLIB).orElseThrow()).lines();
				
				return lines.filter(x -> !x.startsWith("//*")).collect(Collectors.joining(System.lineSeparator()));
			}
	}
}
