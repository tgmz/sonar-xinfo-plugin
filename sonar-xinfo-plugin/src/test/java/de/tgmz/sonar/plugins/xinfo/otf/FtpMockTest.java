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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.mockftpserver.core.command.CommandHandler;
import org.mockftpserver.stub.StubFtpServer;
import org.mockftpserver.stub.command.ListCommandHandler;
import org.mockftpserver.stub.command.RetrCommandHandler;
import org.mockftpserver.stub.command.StorCommandHandler;
import org.mockftpserver.stub.command.SystCommandHandler;

public class FtpMockTest extends AbstractSensorOtfTest {
	private static final String JOB_NAME = "JOB01234";
	private static final String JES_HEADER = "JOBNAME  JOBID    OWNER    STATUS CLASS";
	private static StubFtpServer server;
	
	@BeforeClass
	public static void setupOnce() throws IOException {
		setupServer();
		
		setupEnvironment("ftp", server.getServerControlPort(), 1, true);
	}

	private static void setupServer() throws IOException, FileNotFoundException {
		server = new StubFtpServer();
		server.setServerControlPort(8021);
		
		// Mock successful job submit
		CommandHandler ch = new StorCommandHandler();
		((StorCommandHandler) ch).setFinalReplyText(String.format("It is known to JES as %s", JOB_NAME));
		server.setCommandHandler("STOR", ch);
		
		// Force FTPClient to use a MVSFTPEntryParser to parse the result of LIST command 
		ch = new SystCommandHandler();
		((SystCommandHandler) ch).setSystemName("MVS is the operating system of this server. FTP Server is running on z/OS.");
		server.setCommandHandler("SYST", ch);
		
		// Mock result of LIST 
		ch = new ListCommandHandler();
		((ListCommandHandler) ch).setDirectoryListing(String.format("%s%sFOOB  %s FOO   OUTPUT A        RC=0008 4 spool files", JES_HEADER, System.lineSeparator(), JOB_NAME));
		server.setCommandHandler("LIST", ch);
		
		// Mock XINFO download
		// Use the same result for all job submissions even for ctest.c. It dosen't matter
		ch = new RetrCommandHandler();
		((RetrCommandHandler) ch).setFileContents(IOUtils.toByteArray(new FileReader("testresources/xinfo/plitst.xml"), StandardCharsets.UTF_8));
		server.setCommandHandler("RETR", ch);

		server.start();
	}
}
