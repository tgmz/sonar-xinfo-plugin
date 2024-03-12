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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.server.rule.RulesDefinition;

import de.tgmz.sonar.plugins.xinfo.config.XinfoProjectConfig;
import de.tgmz.sonar.plugins.xinfo.rules.XinfoRuleDefinition;
import de.tgmz.sonar.plugins.xinfo.sensors.ColorizerSensor;
import de.tgmz.sonar.plugins.xinfo.sensors.XinfoCpdSensor;
import de.tgmz.sonar.plugins.xinfo.sensors.XinfoIssuesLoader;

/**
 * Tests for all sensors.
 */
public class SensorTest {
	private static final String LOC = "testresources";
	private static SensorContext sensorContext;
	private static SensorDescriptor sensorDescriptor;
	
	@BeforeClass
	public static void setupOnce() throws IOException {
		MapSettings ms = new MapSettings();
		ms.setProperty(XinfoProjectConfig.XINFO_ROOT, LOC + File.separator +"xinfo");
		ms.setProperty(XinfoProjectConfig.XINFO_LOG_THRESHOLD, "1");
		ms.setProperty(XinfoProjectConfig.XINFO_INCLUDE_LEVEL, "I,W,E,S,U");
		
		File baseDir = new File(LOC);
		
		sensorContext = SensorContextTester.create(baseDir);
		((SensorContextTester) sensorContext).setSettings(ms);
		
		File[] testresources = new File(LOC).listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile();
			}
		});
		
		for (File f : testresources) {
			((SensorContextTester) sensorContext).fileSystem().add(SonarTestFileUtil.create(LOC, f.getName()));
		}
		
		sensorDescriptor = new DefaultSensorDescriptor();
		
		IOUtils.copy(new FileInputStream(new File("testresources/xinfo/broken.xml.txt")), new FileOutputStream(new File("testresources/xinfo/plitst6.xml")));
	}
	
	@AfterClass
	public static void teardownOnce() throws IOException {
		new File("testresources/xinfo/plitst6.xml").delete();
	}
	
	@Test(expected = Test.None.class)
	public void testIssuesLoader() {
		XinfoIssuesLoader issuesLoader = new XinfoIssuesLoader(new CheckFactory(sensorContext.activeRules()));
		
		issuesLoader.describe(sensorDescriptor);
		issuesLoader.execute(sensorContext);
	}

	@Test(expected = Test.None.class)
	public void testColorizer() {
		ColorizerSensor sensor = new ColorizerSensor();
		
		sensor.describe(sensorDescriptor);
		sensor.execute(sensorContext);
	}

	@Test(expected = Test.None.class)
	public void testRulesDefinition() {
		new XinfoRuleDefinition().define(new RulesDefinition.Context());
	}
	
	@Test(expected = Test.None.class)
	public void testCpd() {
		Sensor cpdSensor = new XinfoCpdSensor();
		
		cpdSensor.describe(sensorDescriptor);
		cpdSensor.execute(sensorContext);
	}
}
