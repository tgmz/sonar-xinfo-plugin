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

package de.tgmz.sonar.plugins.xinfo.otf;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.mockserver.configuration.Configuration;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.socket.PortFactory;
import org.mockserver.socket.tls.KeyStoreFactory;

public class ZoweMockTest extends AbstractSensorOtfTest {
	private static ClientAndServer server;
	
	@BeforeClass
	public static void setupOnce() throws IOException {
		setupServer();
		
		setupEnvironment("zowe", server.getPort());
	}

	private static void setupServer() throws IOException {
		HttpsURLConnection.setDefaultSSLSocketFactory(new KeyStoreFactory(Configuration.configuration(), new MockServerLogger()).sslContext().getSocketFactory());
		server = ClientAndServer.startClientAndServer(PortFactory.findFreePort());
		
		try (InputStream is0 = ZoweMockTest.class.getClassLoader().getResourceAsStream("submit0.json");
				InputStream is1 = ZoweMockTest.class.getClassLoader().getResourceAsStream("submit1.json")) {
			server.when(HttpRequest.request().withMethod("PUT")).respond(HttpResponse.response(IOUtils.toString(is0, StandardCharsets.UTF_8)));
			server.when(HttpRequest.request().withMethod("GET")).respond(HttpResponse.response(IOUtils.toString(is1, StandardCharsets.UTF_8)));
		}
		
		server.when(HttpRequest.request().withMethod("POST")).respond(HttpResponse.response().withStatusCode(201));
		server.when(HttpRequest.request().withMethod("DELETE")).respond(HttpResponse.response().withStatusCode(204));
	}
	
}
