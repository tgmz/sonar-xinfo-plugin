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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Configuration;

import de.tgmz.sonar.plugins.xinfo.AbstractXinfoProvider;
import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.config.XinfoFtpConfig;
import de.tgmz.sonar.plugins.xinfo.config.XinfoProjectConfig;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;
import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Loads issues "on-the-fly", i.e. with no stored XINFO files by invoking the appropriate compiler. 
 */
public abstract class AbstractOtfProvider extends AbstractXinfoProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOtfProvider.class);
	
	protected AbstractOtfProvider(Configuration configuration) {
		super(configuration);
	}

	protected PACKAGE createXinfo(InputFile pgm, byte[] xinfo) throws IOException, XinfoException {
		if (getConfiguration().getBoolean(XinfoFtpConfig.XINFO_OTF_STORE_LOCAL).orElse(false)) {
			store(pgm, xinfo);
		}
		
		try (InputStream is = new ByteArrayInputStream(xinfo)) {
			Language lang = Language.getByFilename(pgm.filename());
			
			return (lang == Language.C || lang == Language.CPP) ? super.createXinfoFromEvent(is) : super.createXinfo(is);
		}
	}

	protected String createJcl(Language lang, String inputDsn, String sysxmlsd) throws IOException, XinfoException {
		String template = null;

		switch (lang) {
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
						, inputDsn
						, sysxmlsd
						, getConfiguration().get(XinfoFtpConfig.XINFO_OTF_SYSLIB).orElseThrow()).lines();
				
				return lines.filter(x -> !x.startsWith("//*")).collect(Collectors.joining(System.lineSeparator()));
			}
	}
	private void store(InputFile pgm, byte[] xinfo) {
		Optional<String> oRoot = getConfiguration().get(XinfoProjectConfig.XINFO_ROOT);
		
		if (oRoot.isPresent()) {
			Language lang = Language.getByFilename(pgm.filename());
			
			String xinfoFileName = FilenameUtils.removeExtension(pgm.filename()) 
					+ (lang == Language.C || lang == Language.CPP ? ".event" : ".xml");
			
			try (OutputStream os = new FileOutputStream(oRoot.get() + File.separator + xinfoFileName)) {
				IOUtils.copy(new ByteArrayInputStream(xinfo), os);
			} catch (IOException e) {
				LOGGER.error("Unable to store XINFO locally", e);
			}
		} else {
			LOGGER.warn("No location specified for storing XINFO locally");
		}
	}
}
