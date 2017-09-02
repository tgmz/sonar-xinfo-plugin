/*******************************************************************************
  * Copyright (c) 09.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/

package de.tgmz.sonar.plugins.xinfo;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Simple testcases for RuleFactory
 */
public class RuleFactoryTest {

	@Test
	public void testPli() {
		assertTrue("No PL/I rules found", 0 < RuleFactory.getInstance().getRules(Language.PLI).getRules().size());
	}

	@Test
	public void testCobol() {
		assertTrue("No COBOL rules found", 0 < RuleFactory.getInstance().getRules(Language.COBOL).getRules().size());
	}

	@Test
	public void testAssembler() {
		assertTrue("No HLASM rules found", 0 < RuleFactory.getInstance().getRules(Language.ASSEMBLER).getRules().size());
	}
	
	/**
	 * Test for rule IBM1479I E. This rule is missing in the PL/I programming guide and must be added manualy.
	 */
	@Test
	public void testIBM1479I() {
		boolean result = false;
		
		List<SonarRule> rules = RuleFactory.getInstance().getRules(Language.PLI).getRules();
		
		for (SonarRule sonarRule : rules) {
			if ("IBM1479I E".equals(sonarRule.getKey())) {
				result = true;
				break;
			}
		}
		
		assertTrue("PL/I rule IBM1479I missing", result); 
	}
}


