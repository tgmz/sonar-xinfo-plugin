/*******************************************************************************
  * Copyright (c) 08.08.2017 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.codecoverage;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.output.WriterOutputStream;
import org.junit.Test;

public class ConverterTest {
	@Test
	public void testConvert() throws IOException, ConverterException {
		try (InputStream is = ConverterTest.class.getClassLoader().getResourceAsStream("CCOUTPUT.xml"); StringWriter sw = new StringWriter()) {
			Converter.getInstance().convert(new File("root/pli"), ".pli", is, new WriterOutputStream(sw, StandardCharsets.UTF_8.name()));
			
			assertTrue(sw.getBuffer().length() > 0);
		}
	}
	@Test(expected=ConverterException.class)
	public void testConverterException() throws IOException, ConverterException {
		try (InputStream is = ConverterTest.class.getClassLoader().getResourceAsStream("CCOUTPUT_broken.xml"); StringWriter sw = new StringWriter()) {
			Converter.getInstance().convert(new File("root/pli"), ".pli", is, new WriterOutputStream(sw, StandardCharsets.UTF_8.name()));
		}
	}
}
