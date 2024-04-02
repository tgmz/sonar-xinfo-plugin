/*******************************************************************************
  * Copyright (c) 02.04.2024 Thomas Zierer.
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockftpserver.core.command.CommandHandler;
import org.mockftpserver.stub.StubFtpServer;
import org.mockftpserver.stub.command.ListCommandHandler;
import org.mockftpserver.stub.command.RetrCommandHandler;
import org.mockftpserver.stub.command.StorCommandHandler;
import org.mockftpserver.stub.command.SystCommandHandler;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.internal.MapSettings;

import de.tgmz.sonar.plugins.xinfo.config.XinfoFtpConfig;
import de.tgmz.sonar.plugins.xinfo.config.XinfoProjectConfig;
import de.tgmz.sonar.plugins.xinfo.sensors.XinfoIssuesLoader;

public class SensorOnTheFlyMockTest {
	private static final String LOG_LEVEL_KEY = "org.slf4j.simpleLogger.defaultLogLevel"; 
	private static final String LOC = "otftestresources";
	private static StubFtpServer server;
	private static SensorContext sensorContext;
	private static String logLevel;
	
	@BeforeClass
	public static void setupOnce() throws IOException {
		logLevel = System.getProperty(LOG_LEVEL_KEY, "INFO");
		System.setProperty(LOG_LEVEL_KEY, "DEBUG");	// Force noisy logging in JesProtocolCommandListener

		setupServer();
		
		MapSettings ms = new MapSettings();
		ms.setProperty(XinfoProjectConfig.XINFO_ROOT, LOC + File.separator +"xinfo");
		ms.setProperty(XinfoProjectConfig.XINFO_LOG_THRESHOLD, "1");
		ms.setProperty(XinfoProjectConfig.XINFO_INCLUDE_LEVEL, "I,W,E,S,U");
		ms.setProperty(XinfoFtpConfig.XINFO_OTF, "true");
		ms.setProperty(XinfoFtpConfig.XINFO_OTF_JOBCARD, "");
		ms.setProperty(XinfoFtpConfig.XINFO_OTF_PASS, "bar");
		ms.setProperty(XinfoFtpConfig.XINFO_OTF_SERVER, "localhost");
		ms.setProperty(XinfoFtpConfig.XINFO_OTF_PORT, server.getServerControlPort());
		ms.setProperty(XinfoFtpConfig.XINFO_OTF_USER, "foo");
		ms.setProperty(XinfoFtpConfig.XINFO_OTF_TIMEOUT, "10");
		
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
	}

	private static void setupServer() throws IOException, FileNotFoundException {
		server = new StubFtpServer();
		server.setServerControlPort(8021);
		
		CommandHandler ch = new StorCommandHandler();
		((StorCommandHandler) ch).setFinalReplyText("It is known to JES as JOB01234");
		server.setCommandHandler("STOR", ch);
		
		ch = new SystCommandHandler();
		((SystCommandHandler) ch).setSystemName("MVS is the operating system of this server. FTP Server is running on z/OS.");
		server.setCommandHandler("SYST", ch);
		
		ch = new ListCommandHandler();
		((ListCommandHandler) ch).setDirectoryListing("JOBNAME  JOBID    OWNER    STATUS CLASS" + System.lineSeparator() + "FOOB  JOB01234 FOO   OUTPUT A        RC=0008 4 spool files");
		server.setCommandHandler("LIST", ch);
		
		ch = new RetrCommandHandler();
		((RetrCommandHandler) ch).setFileContents(IOUtils.toByteArray(new FileReader("testresources/xinfo/plitst.xml"), StandardCharsets.UTF_8));
		server.setCommandHandler("RETR", ch);
		
		server.start();
	}
	
	@AfterClass
	public static void teardownOnce() {
		System.setProperty(LOG_LEVEL_KEY, logLevel);
	}
	
	@Test(expected = Test.None.class)
	public void testIssuesLoader() {
		Sensor sensor = new XinfoIssuesLoader(new CheckFactory(sensorContext.activeRules()));
		
		sensor.execute(sensorContext);
	}
}
