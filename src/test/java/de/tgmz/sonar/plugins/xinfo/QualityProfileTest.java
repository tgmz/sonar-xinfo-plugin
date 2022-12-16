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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

import de.tgmz.sonar.plugins.xinfo.languages.AbstractXinfoQualityProfileDefinition;
import de.tgmz.sonar.plugins.xinfo.languages.AssemblerQualityProfileDefinition;
import de.tgmz.sonar.plugins.xinfo.languages.CobolQualityProfileDefinition;
import de.tgmz.sonar.plugins.xinfo.languages.PliQualityProfileDefinition;

/**
 * Simple testcases for QualityProfile.
 */
@RunWith(Parameterized.class)
public class QualityProfileTest {
	private Class<AbstractXinfoQualityProfileDefinition> qp;

	public QualityProfileTest(Class<AbstractXinfoQualityProfileDefinition> qp) {
		super();
		this.qp = qp;
	}

	@Test(expected = Test.None.class)
	public void test() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		qp.getDeclaredConstructor().newInstance().define(new BuiltInQualityProfilesDefinition.Context());
	}
	
	@Parameters(name = "{index}: Check for language [{0}]")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ PliQualityProfileDefinition.class },
				{ CobolQualityProfileDefinition.class },
				{ AssemblerQualityProfileDefinition.class } ,
		};
		return Arrays.asList(data);
	}
}
