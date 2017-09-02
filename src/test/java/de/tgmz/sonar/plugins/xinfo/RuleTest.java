/*******************************************************************************
  * Copyright (c) 10.09.2017 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/

package de.tgmz.sonar.plugins.xinfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;

import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Simple testcases for languages
 */
public class RuleTest {

	@Test
	public void testAnalyzable() throws IOException {
		Iterator<SonarRule> it = RuleFactory.getInstance().getRules(Language.PLI).getRules().iterator();

		SonarRule r = null;
		
		do {
			r = it.next();
		} while (!r.getKey().equals("IBM1479I E"));
		
		assertEquals("IBM1479I E", r.getKey());
		assertEquals("SINGLE", r.getCardinality());
		assertEquals("No description provided", r.getDescription());
		assertEquals("IBM1479I E", r.getInternalKey());
		assertEquals("IBM1479I E", r.getKey());
		assertEquals("Multiple RETURN statements are not allowed under RULES(NOMULTIEXIT). ", r.getName());
		assertNull("IBM1479I E", r.getRemediationFunction());
		assertNull("IBM1479I E", r.getRemediationFunctionBaseEffort());
		assertEquals("CRITICAL", r.getSeverity());
		assertEquals("READY", r.getStatus());
		assertEquals("xinfo", r.getTag().get(0));
		assertEquals("BUG", r.getType());
	}
}
