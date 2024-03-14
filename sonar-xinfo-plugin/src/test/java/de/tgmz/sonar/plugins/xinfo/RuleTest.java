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
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

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
	
	private Class<?> find(String s) {
		Optional<Class<?>> first = RuleFactory.getInstance().getRules().stream().filter(r -> r.getSimpleName().equals(s)).findFirst();
		
		return first.isPresent() ? first.get() : null;
	}
	
	@Parameters(name = "{index}: Check for message [{0}]")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ "IBM1039I"}, {"IBM1479I"}, {"IBM3988I"}, {"IBM1247I"}, {"IBM2848I"}, {"IBM1063I"}, {"IBM1316I"},
		};
		return Arrays.asList(data);
	}
}

