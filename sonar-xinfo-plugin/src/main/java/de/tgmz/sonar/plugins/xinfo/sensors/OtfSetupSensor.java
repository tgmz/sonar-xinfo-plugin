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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Phase;
import org.sonar.api.batch.Phase.Name;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.config.XinfoOtfConfig;
import de.tgmz.sonar.plugins.xinfo.config.XinfoProjectConfig;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.languages.XinfoLanguage;
import de.tgmz.sonar.plugins.xinfo.otf.ConnectionFactory;
import de.tgmz.sonar.plugins.xinfo.otf.IConnectable;
import de.tgmz.sonar.plugins.xinfo.otf.IJob;
import de.tgmz.sonar.plugins.xinfo.otf.JclUtil;

@Phase(name = Name.PRE)
public class OtfSetupSensor implements Sensor {
	private static final Logger LOGGER = LoggerFactory.getLogger(OtfSetupSensor.class);
	private	int ctr = 0;

	@Override
	public void describe(final SensorDescriptor descriptor) {
		descriptor.name("XinfoOtfSetup")
					.onlyOnLanguages(XinfoLanguage.KEY)
					.onlyWhenConfiguration(c -> c.get(XinfoOtfConfig.XINFO_OTF).isPresent());
	}
	
	@Override
	public void execute(final SensorContext context) {
		IConnectable connection = ConnectionFactory.getConnactable(context.config());
		
	    int threshold = context.config().getInt(XinfoProjectConfig.XINFO_LOG_THRESHOLD).orElse(100);
	    
	    try {
			String syslib = context.config().get(XinfoOtfConfig.XINFO_OTF_SYSLIB).orElseThrow();
			
			FilePredicates p = context.fileSystem().predicates();

			for (InputFile inputFile : context.fileSystem().inputFiles(p.hasLanguages(XinfoLanguage.KEY))) {
				Language lang = Language.getByFilename(inputFile.filename()); 
						
				if (lang.isMacro()) {
					String mbr = FilenameUtils.removeExtension(inputFile.filename()).toUpperCase(Locale.getDefault());
					
		            connection.write(String.format("%s(%s)",  syslib, mbr), inputFile.contents());
					
		            writeLog(threshold, inputFile);
				}
				
				if (lang.isMask()) {
					String jcl = createJcl(context.config().get(XinfoOtfConfig.XINFO_OTF_JOBCARD).orElseThrow()
							, FilenameUtils.removeExtension(inputFile.filename()).toUpperCase(Locale.getDefault())
							, syslib
							, inputFile.contents());

			        IJob submit = connection.submit(jcl);
			        
			        if (!context.config().getBoolean(XinfoOtfConfig.XINFO_OTF_KEEP).orElse(false)) {
			        	connection.deleteJob(submit);
			        }
			        
		            writeLog(threshold, inputFile);
				}
			}
	    } catch (IOException | XinfoException e) {
			LOGGER.error("{} failed.", this.getClass().getSimpleName(), e);
	    }
	}

	private void writeLog(int threshold, InputFile inputFile) {
		if (++ctr % threshold == 0) {
			LOGGER.info("{} files processed, current is {}", ctr, inputFile);
		}
	}

	private String createJcl(String jobCard, String mbr, String syslib, String content) throws IOException {
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("elaxfbms.txt");
				Reader r = new InputStreamReader(is, StandardCharsets.UTF_8)) {
			String s = IOUtils.toString(r);
				
			String jcl = MessageFormat.format(s, jobCard, mbr, syslib, content);
					
			return JclUtil.formatJcl(jcl);
		}		
	}
}
