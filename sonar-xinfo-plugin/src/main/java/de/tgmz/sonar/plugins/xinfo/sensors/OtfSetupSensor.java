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
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.languages.XinfoLanguage;
import zowe.client.sdk.core.ZosConnection;
import zowe.client.sdk.rest.Response;
import zowe.client.sdk.rest.exception.ZosmfRequestException;
import zowe.client.sdk.zosfiles.dsn.methods.DsnWrite;

@Phase(name = Name.PRE)
public class OtfSetupSensor implements Sensor {
	private static final Logger LOGGER = LoggerFactory.getLogger(OtfSetupSensor.class);

	@Override
	public void describe(final SensorDescriptor descriptor) {
		descriptor.name("XinfoOtfSetup")
					.onlyOnLanguages(XinfoLanguage.KEY)
					.onlyWhenConfiguration(c -> c.get(XinfoFtpConfig.XINFO_OTF).isPresent());
	}
	
	@Override
	public void execute(final SensorContext context) {
		ZosConnection client = new ZosConnection(context.config().get(XinfoFtpConfig.XINFO_OTF_SERVER).orElseThrow()
				, context.config().get(XinfoFtpConfig.XINFO_OTF_PORT).orElseThrow()
				, context.config().get(XinfoFtpConfig.XINFO_OTF_USER).orElseThrow()
				, context.config().get(XinfoFtpConfig.XINFO_OTF_PASS).orElseThrow());
		
	    int threshold = context.config().getInt(XinfoProjectConfig.XINFO_LOG_THRESHOLD).orElse(100);
	    
	    try {
			String syslib = context.config().get(XinfoFtpConfig.XINFO_OTF_SYSLIB).orElseThrow();
			
			FilePredicates p = context.fileSystem().predicates();

            DsnWrite dsnWrite = new DsnWrite(client);
            Response response;
			
			int ctr = 0;
		
			for (InputFile inputFile : context.fileSystem().inputFiles(p.hasLanguages(XinfoLanguage.KEY))) {
				if (Language.getByFilename(inputFile.filename()).isMacro()) {
		            response = dsnWrite.write(syslib, FilenameUtils.removeExtension(inputFile.filename()).toUpperCase(Locale.getDefault()), inputFile.contents());
				
		            LOGGER.debug("Response for uploading {} is {}", inputFile, response);
		            
					if (++ctr % threshold == 0) {
						LOGGER.info("{} files processed, current is {}", ctr, inputFile);
					}
				}
			}
	    } catch (IOException | ZosmfRequestException e) {
			LOGGER.error("{} failed.", this.getClass().getSimpleName(), e);
	    }
	}
}
