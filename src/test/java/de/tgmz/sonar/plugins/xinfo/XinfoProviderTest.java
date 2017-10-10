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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.sonar.api.config.MapSettings;
import org.sonar.api.config.Settings;

import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.plicomp.PACKAGE;

public class XinfoProviderTest {
	private static final Settings SETTINGS = new MapSettings();
	
	/**
	 * Dummy to access the createXinfo(InputStream) method.
	 */
	private static class DummyXinfoProvider extends AbstractXinfoProvider {
		public DummyXinfoProvider() {
			super(new MapSettings());
		}
		@Override
		public PACKAGE getXinfo(IXinfoAnalyzable pgm) throws XinfoException {
			return null;
		}
	}
	
	@Test
	public void test() throws XinfoException, FileNotFoundException {
		assertEquals(1, XinfoProviderFactory.getProvider(SETTINGS).getXinfo(new XinfoFileAnalyzable(Language.PLI, new File("testresources/plitest.pli"))).getMESSAGE().size());
	}

	@Test
	public void testPliNoXinfo() throws XinfoException, FileNotFoundException {
		assertEquals(0, XinfoProviderFactory.getProvider(SETTINGS).getXinfo(new XinfoFileAnalyzable(Language.PLI, new File("testresources/plitest3.pli"))).getMESSAGE().size());
	}

	@Test
	public void testPliMultipleXinfo() throws XinfoException, FileNotFoundException {
		assertEquals(0, XinfoProviderFactory.getProvider(SETTINGS).getXinfo(new XinfoFileAnalyzable(Language.PLI, new File("testresources/plitest2.pli"))).getMESSAGE().size());
	}

	@Test
	public void testMessageInInclude() throws XinfoException, FileNotFoundException {
		assertEquals(2, XinfoProviderFactory.getProvider(SETTINGS).getXinfo(new XinfoFileAnalyzable(Language.PLI, new File("testresources/plitest5.pli"))).getMESSAGE().size());
	}

	@Test(expected=XinfoException.class)
	public void testClosedInputStream() throws XinfoException, IOException {
		FileInputStream fis = new FileInputStream("testresources/xml/plitest.xml");
		fis.close();
		
		assertNotNull(new DummyXinfoProvider().createXinfo(fis));
	}

	@Test
	public void testWrongEncoding() throws XinfoException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		IOUtils.copy(new FileInputStream("testresources/xml/plitest.xml"), baos);
		
		String s = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		
		s = s.replace("UTF-8", "foobar");
		
		assertNotNull(new DummyXinfoProvider().createXinfo(IOUtils.toInputStream(s, StandardCharsets.UTF_8)));
	}
	
	@Test(expected=XinfoException.class)
	public void testUnparseable() throws XinfoException, IOException {
		String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><p>";
		
		assertNotNull(new DummyXinfoProvider().createXinfo(IOUtils.toInputStream(s, StandardCharsets.UTF_8)));
	}
	
	@Test(expected=XinfoException.class)
	public void testUnparseableWrongEncoding() throws XinfoException, IOException {
		String s = "<?xml version=\"1.0\" encoding=\"foobar\"?><p>";
		
		assertNotNull(new DummyXinfoProvider().createXinfo(IOUtils.toInputStream(s, StandardCharsets.UTF_8)));
	}
}
