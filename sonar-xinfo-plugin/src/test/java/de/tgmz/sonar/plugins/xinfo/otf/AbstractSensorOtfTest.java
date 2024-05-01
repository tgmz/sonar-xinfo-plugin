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
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.internal.MapSettings;

import de.tgmz.sonar.plugins.xinfo.SonarTestFileUtil;
import de.tgmz.sonar.plugins.xinfo.config.XinfoOtfConfig;
import de.tgmz.sonar.plugins.xinfo.config.XinfoProjectConfig;
import de.tgmz.sonar.plugins.xinfo.sensors.OtfSetupSensor;
import de.tgmz.sonar.plugins.xinfo.sensors.XinfoIssuesLoader;

public abstract class AbstractSensorOtfTest {
	private static final String LOG_LEVEL_KEY = "org.slf4j.simpleLogger.defaultLogLevel"; 
	private static final String LOC = "otftestresources";
	private static final String LOC_XINFO = LOC + File.separator +"xinfo";
	private static SensorContext sensorContext;
	private static String logLevel;
	
	protected static void setupEnvironment(String provider, int port, boolean clean) throws IOException {
		logLevel = System.getProperty(LOG_LEVEL_KEY, "INFO");
		System.setProperty(LOG_LEVEL_KEY, "DEBUG");	// Force noisy logging
		
		if (clean) {
			FileUtils.deleteDirectory(new File(LOC_XINFO));
		}

		MapSettings ms = new MapSettings();
		ms.setProperty(XinfoProjectConfig.XINFO_ROOT, LOC_XINFO);
		ms.setProperty(XinfoProjectConfig.XINFO_LOG_THRESHOLD, "1");
		ms.setProperty(XinfoProjectConfig.XINFO_NUM_THREADS, "1");
		
		ms.setProperty(XinfoOtfConfig.XINFO_OTF, provider);
		ms.setProperty(XinfoOtfConfig.XINFO_OTF_PORT, port);

		ms.setProperty(XinfoOtfConfig.XINFO_OTF_JOBCARD, System.getProperty(XinfoOtfConfig.XINFO_OTF_JOBCARD, ""));
		ms.setProperty(XinfoOtfConfig.XINFO_OTF_PASS, System.getProperty(XinfoOtfConfig.XINFO_OTF_PASS, "bar"));
		ms.setProperty(XinfoOtfConfig.XINFO_OTF_SERVER, System.getProperty(XinfoOtfConfig.XINFO_OTF_SERVER, "localhost"));
		ms.setProperty(XinfoOtfConfig.XINFO_OTF_USER, System.getProperty(XinfoOtfConfig.XINFO_OTF_USER, "foo"));
		ms.setProperty(XinfoOtfConfig.XINFO_OTF_TIMEOUT, System.getProperty(XinfoOtfConfig.XINFO_OTF_TIMEOUT, "10"));
		ms.setProperty(XinfoOtfConfig.XINFO_OTF_SYSLIB, System.getProperty(XinfoOtfConfig.XINFO_OTF_SYSLIB, "HLQ.XINFO.MACLIB"));
		ms.setProperty(XinfoOtfConfig.XINFO_OTF_STORE_LOCAL, "true");
		
		File baseDir = new File(LOC);
		
		sensorContext = SensorContextTester.create(baseDir);
		((SensorContextTester) sensorContext).setSettings(ms);
		
		File[] testresources = new File(LOC).listFiles(c -> c.isFile());
		
		for (File f : testresources) {
			((SensorContextTester) sensorContext).fileSystem().add(SonarTestFileUtil.create(LOC, f.getName()));
		}
	}

	
	@AfterClass
	public static void teardownOnce() {
		System.setProperty(LOG_LEVEL_KEY, logLevel);
	}
	
	@Test(expected = Test.None.class)
	public void testSensors() {
		SensorDescriptor sd = new DefaultSensorDescriptor();
		Sensor sensor = new OtfSetupSensor();
		
		sensor.describe(sd);
		sensor.execute(sensorContext);
		
		sensor = new XinfoIssuesLoader(new CheckFactory(sensorContext.activeRules()));
		
		sensor.describe(sd);
		sensor.execute(sensorContext);
	}
}