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

import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class XinfoProviderUnparseableTest {
	private String xml;
	
	public XinfoProviderUnparseableTest(String xml) {
		super();
		this.xml = xml;
	}
	@Test
	public void testUnparseable() throws IOException {
		assertThrows(XinfoException.class, () -> new DummyXinfoProvider().createXinfo(IOUtils.toInputStream(xml, StandardCharsets.UTF_8)));
	}
	
	@Test
	public void testUnmarshalable() throws IOException {
		assertThrows(XinfoException.class, () -> new DummyXinfoProvider().createXinfo(IOUtils.toInputStream(xml, StandardCharsets.UTF_8)));
	}
	
	@Test
	public void testUnparseableWrongEncoding() throws IOException {
		assertThrows(XinfoException.class, () -> new DummyXinfoProvider().createXinfo(IOUtils.toInputStream(xml, StandardCharsets.UTF_8)));
	}
	@Parameters(name = "{index}: Check for language [{0}]")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ "<?xml version=\"1.0\" encoding=\"UTF-8\"?><p>" },
				{ "<?xml version=\"1.0\" encoding=\"UTF-8\"?><p></p>" },
				{ "<?xml version=\"1.0\" encoding=\"foobar\"?><p>" } ,
		};
		return Arrays.asList(data);
	}
}
