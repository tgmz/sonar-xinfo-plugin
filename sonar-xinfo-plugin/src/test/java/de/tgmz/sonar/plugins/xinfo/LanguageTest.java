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

import java.io.File;

import org.junit.Test;
import org.junit.Test.None;
import org.sonar.api.batch.sensor.internal.SensorContextTester;

import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.languages.XinfoLanguage;

/**
 * Simple testcases for languages
 */
public class LanguageTest {
	private static final String LOC = "testresources";
	@Test
	public void testExtensions() {
		assertNotNull(new XinfoLanguage(SensorContextTester.create(new File(LOC)).config()).getFileSuffixes());
	}
	
	@Test(expected=None.class)
	public void testLanguageByFile() {
		Language.getByFilename("temp.pli");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLanguageByFileError() {
		Language.getByFilename("temp.php");
	}
}
