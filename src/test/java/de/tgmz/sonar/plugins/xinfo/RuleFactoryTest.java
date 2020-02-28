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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Simple testcases for RuleFactory
 */
@RunWith(Parameterized.class)
public class RuleFactoryTest {
	private Language lang;
	
	public RuleFactoryTest(Language lang) {
		super();
		this.lang = lang;
	}

	@Test
	public void test() {
		assertTrue("No " + lang.getName() + " rules found", 0 < RuleFactory.getInstance().getRules(lang).size());
	}
	
	@Parameters(name = "{index}: Check for language [{0}]")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ Language.PLI }, { Language.ASSEMBLER }, { Language.COBOL } ,
		};
		return Arrays.asList(data);
	}
}


