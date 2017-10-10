/*******************************************************************************
  * Copyright (c) 10.09.2017 Thomas Zierer.
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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Simple testcases for languages
 */
public class AnalyzableTest {

	@Test
	public void testAnalyzable() throws IOException {
		File f = new File("testresources/plitest.pli");
		
		String s = IOUtils.toString(new FileReader(f));
		
		XinfoFileAnalyzable fa = new XinfoFileAnalyzable(Language.PLI, f);
		
		assertEquals(s, fa.getSource());
		assertEquals(Language.PLI, fa.getLanguage());
		assertEquals("PLITEST", fa.getName());
	}
}
