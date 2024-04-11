/*******************************************************************************
  * Copyright (c) 20.03.2024 Thomas Zierer.
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
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Phase;
import org.sonar.api.batch.Phase.Name;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

import de.tgmz.sonar.plugins.xinfo.config.XinfoFtpConfig;
import de.tgmz.sonar.plugins.xinfo.config.XinfoProjectConfig;
import de.tgmz.sonar.plugins.xinfo.ftp.JesClient;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.languages.XinfoLanguage;

@Phase(name = Name.PRE)
public class OtfSetupSensor implements Sensor {
	private static final Logger LOGGER = LoggerFactory.getLogger(OtfSetupSensor.class);

	private FTPClient client;
	
	public OtfSetupSensor() {
		client = new JesClient();	// We do not need the JES specific methods but this way we can reuse 
									// the JesProtocolCommandListener which comes handy with logging
	    client.configure(new FTPClientConfig(FTPClientConfig.SYST_MVS));
	}
	
	@Override
	public void describe(final SensorDescriptor descriptor) {
		descriptor.name("XinfoOtfSetup")
					.onlyOnLanguages(XinfoLanguage.KEY)
					.onlyWhenConfiguration(c -> c.getBoolean(XinfoFtpConfig.XINFO_OTF).orElse(false));
	}
	
	@Override
	public void execute(final SensorContext context) {
	    int threshold = context.config().getInt(XinfoProjectConfig.XINFO_LOG_THRESHOLD).orElse(100);
	    
	    try {
	    	client.connect(context.config().get(XinfoFtpConfig.XINFO_OTF_SERVER).orElseThrow()
	    				, context.config().getInt(XinfoFtpConfig.XINFO_OTF_PORT).orElse(21));
	    	
	    	if (!client.login(context.config().get(XinfoFtpConfig.XINFO_OTF_USER).orElseThrow()
	    			, context.config().get(XinfoFtpConfig.XINFO_OTF_PASS).orElseThrow())) {
	    		LOGGER.error("Login unsuccessful");
	    		
	    		return;
	    	}
	    	
			client.setFileType(FTP.ASCII_FILE_TYPE);
			client.enterLocalPassiveMode();
			
			String syslib = context.config().get(XinfoFtpConfig.XINFO_OTF_SYSLIB).orElseThrow();
			
			if (!client.changeWorkingDirectory("//" + syslib)) {
	    		LOGGER.error("Cannot change working directory to SYSLIB {}", syslib);
	    		
	    		return;
			}
			
			if (!client.getReplyString().contains("is a partitioned data set")) {
				LOGGER.info("SYSLIB {} does not exist or is not a partitioned dataset, allocating new", syslib);
				
				allocateSyslib(syslib);
			}

			FilePredicates p = context.fileSystem().predicates();
		
			int ctr = 0;
		
			for (InputFile inputFile : context.fileSystem().inputFiles(p.hasLanguages(XinfoLanguage.KEY))) {
				if (Language.getByFilename(inputFile.filename()).isMacro()) {
					client.storeFile(FilenameUtils.removeExtension(inputFile.filename()).toUpperCase(Locale.getDefault()), inputFile.inputStream());
				
					if (++ctr % threshold == 0) {
						LOGGER.info("{} files processed, current is {}", ctr, inputFile);
					}
				}
			}
			
			client.logout();
	    } catch (IOException e) {
			LOGGER.error("{} failed.", this.getClass().getSimpleName(), e);
	    }
	}
	private void allocateSyslib(String s) throws IOException {
		client.site("FILETYPE=SEQ");
		client.site("PDSTYPE=PDSE");		// Use PDS type from the SMS data class
		client.site("LRECL=80");
		client.site("PRIMARY=100");
		client.site("SECONDARY=100");
		client.site("DIRECTORY=10");
		
		int i = s.lastIndexOf('.');
		
		String parent = i > 0 ? s.substring(0, i) : "";
		String dir = s.substring(i + 1);
		
		client.changeWorkingDirectory("//" + parent);
		
		int mkdir = client.mkd(dir);
		
		if (FTPReply.isPositiveCompletion(mkdir)) {
			LOGGER.info("New SYSLIB {} allocated", s);
		} else {
			throw new IOException(String.format("SYSLIB %s not allocated, reason is %d %s", s, mkdir, client.getReplyString()));
		}
		
		client.changeWorkingDirectory(dir);
	}
}
