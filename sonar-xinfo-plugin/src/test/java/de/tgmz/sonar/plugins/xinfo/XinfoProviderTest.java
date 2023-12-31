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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class XinfoProviderTest  {
	@Test
	public void testClosedInputStream() throws IOException {
		FileInputStream fis = new FileInputStream("testresources/xml/plitest.xml");
		fis.close();
		
		assertThrows(XinfoException.class, () -> new DummyXinfoProvider().createXinfo(fis));
	}

	@Test
	public void testWrongEncoding() throws XinfoException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		IOUtils.copy(new FileInputStream("testresources/xml/plitest.xml"), baos);
		
		String s = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		
		s = s.replace("UTF-8", "foobar");
		
		assertNotNull(new DummyXinfoProvider().createXinfo(IOUtils.toInputStream(s, StandardCharsets.UTF_8)));
	}
}
