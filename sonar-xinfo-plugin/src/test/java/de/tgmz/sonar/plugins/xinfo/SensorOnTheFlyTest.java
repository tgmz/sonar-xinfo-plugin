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

package de.tgmz.sonar.plugins.xinfo;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.internal.MapSettings;

import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.sensors.AssemblerIssuesLoader;
import de.tgmz.sonar.plugins.xinfo.sensors.CCPPIssuesLoader;
import de.tgmz.sonar.plugins.xinfo.sensors.CobolIssuesLoader;
import de.tgmz.sonar.plugins.xinfo.sensors.PliIssuesLoader;

/**
 * Tests for all sensors.
 */
public class SensorOnTheFlyTest {
	private static final String LOC = "testresources";
	private static SensorContext sensorContext;
	private static SensorDescriptor sensorDescriptor;
	
	@BeforeClass
	public static void setupOnce() throws IOException {
		MapSettings ms = new MapSettings();
		ms.setProperty(XinfoConfig.XINFO_ROOT, LOC + File.separator +"xinfo");
		ms.setProperty(XinfoConfig.XINFO_LOG_THRESHOLD, "1");
		ms.setProperty(XinfoConfig.XINFO_INCLUDE_LEVEL, "I,W,E,S,U");
		ms.setProperty(XinfoConfig.XINFO_OTF, "true");
		ms.setProperty(XinfoConfig.XINFO_OTF_JOBCARD, System.getProperty(XinfoConfig.XINFO_OTF_JOBCARD));
		ms.setProperty(XinfoConfig.XINFO_OTF_PASS, System.getProperty(XinfoConfig.XINFO_OTF_PASS));
		ms.setProperty(XinfoConfig.XINFO_OTF_SERVER, System.getProperty(XinfoConfig.XINFO_OTF_SERVER));
		ms.setProperty(XinfoConfig.XINFO_OTF_PORT, System.getProperty(XinfoConfig.XINFO_OTF_PORT));
		ms.setProperty(XinfoConfig.XINFO_OTF_USER, System.getProperty(XinfoConfig.XINFO_OTF_USER));
		ms.setProperty(XinfoConfig.XINFO_OTF_TIMEOUT, System.getProperty(XinfoConfig.XINFO_OTF_TIMEOUT));
		
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
			((SensorContextTester) sensorContext).fileSystem().add(SonarTestFileUtil.create(LOC, f.getName(), Language.getByExtension(f.getName())));
		}
		
		sensorDescriptor = new DefaultSensorDescriptor();
	}
	
	@Test(expected = Test.None.class)
	public void testPli() {
		PliIssuesLoader issuesLoader = new PliIssuesLoader(sensorContext.fileSystem());
		
		issuesLoader.describe(sensorDescriptor);
		issuesLoader.execute(sensorContext);
	}

	@Test(expected = Test.None.class)
	public void testCobol() {
		CobolIssuesLoader issuesLoader = new CobolIssuesLoader(sensorContext.fileSystem());
		
		issuesLoader.describe(sensorDescriptor);
		issuesLoader.execute(sensorContext);
	}

	@Test(expected = Test.None.class)
	public void testAssember() {
		AssemblerIssuesLoader issuesLoader = new AssemblerIssuesLoader(sensorContext.fileSystem());
		
		issuesLoader.describe(sensorDescriptor);
		issuesLoader.execute(sensorContext);
	}

	@Test(expected = Test.None.class)
	public void testCCPP() {
		CCPPIssuesLoader issuesLoader = new CCPPIssuesLoader(sensorContext.fileSystem());
		
		issuesLoader.describe(sensorDescriptor);
		issuesLoader.execute(sensorContext);
	}
}
