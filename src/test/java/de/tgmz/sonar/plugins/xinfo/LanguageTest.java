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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.tgmz.sonar.plugins.xinfo.languages.AssemblerLanguage;
import de.tgmz.sonar.plugins.xinfo.languages.CobolLanguage;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.languages.PliLanguage;

/**
 * Simple testcases for languages
 */
public class LanguageTest {

	@Test
	public void testExtensions() {
		assertArrayEquals(new String[] {"pli", "pl1", "inc"}, new PliLanguage().getFileSuffixes());
		assertArrayEquals(new String[] {"cbl", "cob", "cpy"}, new CobolLanguage().getFileSuffixes());
		assertArrayEquals(new String[] {"asm", "mac"}, new AssemblerLanguage().getFileSuffixes());
	}

	@Test
	public void testLanguages() {
		assertEquals(Language.ASSEMBLER, Language.getByKey("asm"));
		assertEquals(Language.COBOL, Language.getByKey("cbl"));
		assertEquals(Language.PLI, Language.getByKey("pli"));
		
		assertEquals("xinfo-pli", Language.PLI.getRepoKey());
		assertEquals("Xinfo PL/I", Language.PLI.getRepoName());
		
		assertEquals("xinfo-cbl", Language.COBOL.getRepoKey());
		assertEquals("Xinfo COBOL", Language.COBOL.getRepoName());
		
		assertEquals("xinfo-asm", Language.ASSEMBLER.getRepoKey());
		assertEquals("Xinfo Assembler", Language.ASSEMBLER.getRepoName());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testLanguageWrongKey() {
		Language.getByKey("wrong");
	}
}
