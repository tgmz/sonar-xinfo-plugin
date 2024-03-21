/*******************************************************************************
  * Copyright (c) 21.03.2024 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.ftp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockftpserver.stub.StubFtpServer;

public class JesClientTest {
	private static final String LOG_LEVEL_KEY = "org.slf4j.simpleLogger.defaultLogLevel"; 
	private static String logLevel;
	private static StubFtpServer server;

	@BeforeClass
	public static void setupOnce() {
		logLevel = System.getProperty(LOG_LEVEL_KEY, "INFO");
		System.setProperty(LOG_LEVEL_KEY, "DEBUG");	// Force noisy logging in JesProtocolCommandListener 
		
		server = new StubFtpServer();
		server.start();
	}
	
	@AfterClass
	public static void teardownOnce() {
		server.stop();
		
		System.setProperty(LOG_LEVEL_KEY, logLevel);
	}
	
	@Test
	public void testClient() throws IOException {
		JesClient client = new JesClient();
		client.connect("localhost", server.getServerControlPort());
		
		assertTrue(client.login("foo", "bar"));
		client.submit("");
		assertEquals(200, client.setOwnerFilter(""));
		assertNotNull(client.listJobsDetailed());
	}
}
