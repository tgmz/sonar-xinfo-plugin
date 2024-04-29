/*******************************************************************************
  * Copyright (c) 29.04.2024 Thomas Zierer.
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
import java.util.regex.Pattern;

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

public class OtfProvider extends AbstractXinfoProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(OtfProvider.class);
	private static final Pattern P_DB2 = Pattern.compile("EXEC\\s+SQL", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	private static final Pattern P_CICS = Pattern.compile("EXEC\\s+CICS", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	private IConnectable connection;
	
	public OtfProvider(Configuration configuration) {
		super(configuration);
		
		connection = ConnectionFactory.getConnactable(configuration);
	}
	@Override
	public PACKAGE getXinfo(InputFile pgm) throws XinfoException {
		try {
			String inputDsn = connection.createAndUploadInputDataset(Language.getByFilename(pgm.filename()), pgm.contents());
			
			String sysxmlsd = connection.createSysxml();

			String jcl = createJcl(pgm, inputDsn, sysxmlsd);
			
			connection.submit(jcl);
			
			byte[] bs = connection.retrieve(sysxmlsd);
			
			connection.deleteDsn(sysxmlsd);
			
			if (getConfiguration().getBoolean(XinfoFtpConfig.XINFO_OTF_STORE_LOCAL).orElse(false)) {
				store(pgm, bs);
			}
			
			InputStream is = new ByteArrayInputStream(bs);
			
			Language lang = Language.getByFilename(pgm.filename());
				
			return (lang == Language.C || lang == Language.CPP) ? super.createXinfoFromEvent(is) : super.createXinfo(is);
		} catch (IOException e) {
			throw new XinfoException("Error in communication", e);
		}
	}
	private String createJcl(InputFile pgm, String inputDsn, String sysxmlsd) throws IOException {
		String template = null;

		String comp = "";
		String db2 = getDb2(pgm); 
		String cics = getCics(pgm);

		switch (Language.getByFilename(pgm.filename())) {
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
			
			String jcl = MessageFormat.format(s
					, getConfiguration().get(XinfoFtpConfig.XINFO_OTF_JOBCARD).orElseThrow()
					, inputDsn
					, sysxmlsd
					, getConfiguration().get(XinfoFtpConfig.XINFO_OTF_SYSLIB).orElseThrow()
					, comp
					, db2
					, cics);
				
			return JclUtil.formatJcl(jcl);
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
	private String getDb2(InputFile pgm) throws IOException {
		if (P_DB2.matcher(pgm.contents()).find()) {
			return Language.getByFilename(pgm.filename()) == Language.PLI ? "',PP(SQL)'" : "',SQL'";
		} else {
			return "";
		}
	}
	private String getCics(InputFile pgm) throws IOException {
		if (P_CICS.matcher(pgm.contents()).find()) {
			return Language.getByFilename(pgm.filename()) == Language.PLI ? "',PP(CICS)'" : "',CICS'";
		} else {
			return "";
		}
	}
}
