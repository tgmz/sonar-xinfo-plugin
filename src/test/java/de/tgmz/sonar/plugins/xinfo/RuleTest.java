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
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Simple testcases for rules
 */
@RunWith(value = Parameterized.class)
public class RuleTest {
	private String msg;
	
	public RuleTest(String msg) {
		super();
		this.msg = msg;
	}
	
	@Test
	public void testAnalyzable() throws IOException {
		assertNotNull(find(msg));
	}
	
	private Rule find(String s) {
		Iterator<Rule> it = RuleFactory.getInstance().getRules(Language.PLI).getRule().iterator();
		
		Rule r = null;
		
		do {
			r = it.next();
		} while (!r.getKey().equals(s));
		
		return r;
	}
	
	@Parameters(name = "{index}: Check for message [{0}]")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ "IBM1039I I"}, {"IBM1479I E"}, {"IBM3988I S"}, {"IBM1247I E"}, {"MC00016"},
		};
		return Arrays.asList(data);
	}
}

