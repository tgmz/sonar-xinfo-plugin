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
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.configuration.Configuration;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.socket.PortFactory;
import org.mockserver.socket.tls.KeyStoreFactory;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.internal.MapSettings;

import de.tgmz.sonar.plugins.xinfo.config.XinfoFtpConfig;
import de.tgmz.sonar.plugins.xinfo.config.XinfoProjectConfig;
import de.tgmz.sonar.plugins.xinfo.sensors.XinfoIssuesLoader;

public class SensorZoweMockTest {
	private static final String LOG_LEVEL_KEY = "org.slf4j.simpleLogger.defaultLogLevel"; 
	private static final String LOC = "otftestresources";
	private static ClientAndServer server;
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
		ms.setProperty(XinfoProjectConfig.XINFO_NUM_THREADS, "1");
		ms.setProperty(XinfoFtpConfig.XINFO_OTF, "zowe");
		ms.setProperty(XinfoFtpConfig.XINFO_OTF_JOBCARD, "");
		ms.setProperty(XinfoFtpConfig.XINFO_OTF_PASS, "bar");
		ms.setProperty(XinfoFtpConfig.XINFO_OTF_SERVER, "localhost");
		ms.setProperty(XinfoFtpConfig.XINFO_OTF_PORT, server.getPort());
		ms.setProperty(XinfoFtpConfig.XINFO_OTF_USER, "foo");
		ms.setProperty(XinfoFtpConfig.XINFO_OTF_TIMEOUT, "10");
		ms.setProperty(XinfoFtpConfig.XINFO_OTF_SYSLIB, "FOO.XNFO.SYSLIB");
		
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

	private static void setupServer() throws IOException {
		HttpsURLConnection.setDefaultSSLSocketFactory(new KeyStoreFactory(Configuration.configuration(), new MockServerLogger()).sslContext().getSocketFactory());
		server = ClientAndServer.startClientAndServer(PortFactory.findFreePort());
		
		try (InputStream is0 = SensorZoweMockTest.class.getClassLoader().getResourceAsStream("submit0.json");
				InputStream is1 = SensorZoweMockTest.class.getClassLoader().getResourceAsStream("submit1.json")) {
			server.when(HttpRequest.request().withMethod("PUT")).respond(HttpResponse.response(IOUtils.toString(is0, StandardCharsets.UTF_8)));
			server.when(HttpRequest.request().withMethod("GET")).respond(HttpResponse.response(IOUtils.toString(is1, StandardCharsets.UTF_8)));
		}
		
		server.when(HttpRequest.request().withMethod("DELETE")).respond(HttpResponse.response().withStatusCode(204));
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
