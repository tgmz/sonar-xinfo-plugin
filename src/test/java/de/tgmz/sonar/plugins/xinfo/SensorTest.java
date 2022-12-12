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
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.server.rule.RulesDefinition;

import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.rules.XinfoRulesDefinition;
import de.tgmz.sonar.plugins.xinfo.sensors.AssemblerColorizer;
import de.tgmz.sonar.plugins.xinfo.sensors.AssemblerIssuesLoader;
import de.tgmz.sonar.plugins.xinfo.sensors.CobolColorizer;
import de.tgmz.sonar.plugins.xinfo.sensors.CobolIssuesLoader;
import de.tgmz.sonar.plugins.xinfo.sensors.CpdTokenizerSensor;
import de.tgmz.sonar.plugins.xinfo.sensors.PliColorizer;
import de.tgmz.sonar.plugins.xinfo.sensors.PliIssuesLoader;

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
		ms.setProperty(XinfoConfig.XINFO_ROOT, LOC + File.separator +"xml");
		ms.setProperty(XinfoConfig.XINFO_EXTRA, "true");
		
		File baseDir = new File(LOC);
		
		sensorContext = SensorContextTester.create(baseDir);
		((SensorContextTester) sensorContext).setSettings(ms);
		
		((SensorContextTester) sensorContext).fileSystem().add(SonarTestFileUtil.create(LOC, "plitest.pli", Language.PLI));
		((SensorContextTester) sensorContext).fileSystem().add(SonarTestFileUtil.create(LOC, "plitest5.pli", Language.PLI));
		((SensorContextTester) sensorContext).fileSystem().add(SonarTestFileUtil.create(LOC, "plitest6.pli", Language.PLI));
		((SensorContextTester) sensorContext).fileSystem().add(SonarTestFileUtil.create(LOC, "plitest7.pli", Language.PLI));
		((SensorContextTester) sensorContext).fileSystem().add(SonarTestFileUtil.create(LOC, "plitest8.pli", Language.PLI));
		((SensorContextTester) sensorContext).fileSystem().add(SonarTestFileUtil.create(LOC, "plitest9.pli", Language.PLI));
		((SensorContextTester) sensorContext).fileSystem().add(SonarTestFileUtil.create(LOC, "asmtest.asm", Language.ASSEMBLER));
		((SensorContextTester) sensorContext).fileSystem().add(SonarTestFileUtil.create(LOC, "cobtest.cbl", Language.COBOL));
		
		sensorDescriptor = new DefaultSensorDescriptor();
		
		IOUtils.copy(new FileInputStream(new File("testresources/xml/broken.xml.txt")), new FileOutputStream(new File("testresources/xml/plitest6.xml")));
	}
	
	@AfterClass
	public static void teardownOnce() throws IOException {
		new File("testresources/xml/plitest6.xml").delete();
	}
	
	@Test(expected = Test.None.class)
	public void testPli() {
		PliColorizer colorizer = new PliColorizer();
		PliIssuesLoader issuesLoader = new PliIssuesLoader(sensorContext.fileSystem());
		
		colorizer.describe(sensorDescriptor);
		colorizer.execute(sensorContext);
		
		issuesLoader.describe(sensorDescriptor);
		issuesLoader.execute(sensorContext);
	}

	@Test(expected = Test.None.class)
	public void testCobol() {
		CobolColorizer colorizer = new CobolColorizer();
		CobolIssuesLoader issuesLoader = new CobolIssuesLoader(sensorContext.fileSystem());
		
		colorizer.describe(sensorDescriptor);
		colorizer.execute(sensorContext);
		
		issuesLoader.describe(sensorDescriptor);
		issuesLoader.execute(sensorContext);
	}

	@Test(expected = Test.None.class)
	public void testAssember() {
		AssemblerColorizer colorizer = new AssemblerColorizer();
		AssemblerIssuesLoader issuesLoader = new AssemblerIssuesLoader(sensorContext.fileSystem());
		
		colorizer.describe(sensorDescriptor);
		colorizer.execute(sensorContext);
		
		issuesLoader.describe(sensorDescriptor);
		issuesLoader.execute(sensorContext);
	}

	@Test(expected = Test.None.class)
	public void testRulesDefinition() {
		new XinfoRulesDefinition().define(new RulesDefinition.Context());
	}
	
	@Test(expected = Test.None.class)
	public void testCpd() {
		CpdTokenizerSensor cpdSensor = new CpdTokenizerSensor();
		
		cpdSensor.describe(sensorDescriptor);
		cpdSensor.execute(sensorContext);
	}
}
