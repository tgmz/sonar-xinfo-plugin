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
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.internal.ConfigurationBridge;
import org.sonar.api.config.internal.MapSettings;

import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;

public class XinfoProviderTest {
	/**
	 * Dummy to access the createXinfo(InputStream) method.
	 */
	private static class DummyXinfoProvider extends AbstractXinfoProvider {
		public DummyXinfoProvider() {
			super(new ConfigurationBridge(new MapSettings()));
		}
		@Override
		public PACKAGE getXinfo(InputFile pgm) throws XinfoException {
			return null;
		}
	}
	
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
	
	@Test
	public void testUnparseable() throws IOException {
		String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><p>";
		
		assertThrows(XinfoException.class, () -> new DummyXinfoProvider().createXinfo(IOUtils.toInputStream(s, StandardCharsets.UTF_8)));
	}
	
	@Test
	public void testUnmarshalable() throws IOException {
		String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><p></p>";
		
		assertThrows(XinfoException.class, () -> new DummyXinfoProvider().createXinfo(IOUtils.toInputStream(s, StandardCharsets.UTF_8)));
	}
	
	@Test
	public void testUnparseableWrongEncoding() throws IOException {
		String s = "<?xml version=\"1.0\" encoding=\"foobar\"?><p>";
		
		assertThrows(XinfoException.class, () -> new DummyXinfoProvider().createXinfo(IOUtils.toInputStream(s, StandardCharsets.UTF_8)));
	}
}
