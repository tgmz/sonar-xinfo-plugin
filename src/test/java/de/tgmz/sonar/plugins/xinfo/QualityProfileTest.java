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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.tgmz.sonar.plugins.xinfo.languages.AbstractXinfoQualityProfile;
import de.tgmz.sonar.plugins.xinfo.languages.AssemblerQualityProfile;
import de.tgmz.sonar.plugins.xinfo.languages.CobolQualityProfile;
import de.tgmz.sonar.plugins.xinfo.languages.PliQualityProfile;

/**
 * Simple testcases for QualityProfile.
 */
@RunWith(Parameterized.class)
public class QualityProfileTest {
	private Class<AbstractXinfoQualityProfile> qp;

	public QualityProfileTest(Class<AbstractXinfoQualityProfile> qp) {
		super();
		this.qp = qp;
	}

	@Test
	public void test() throws InstantiationException, IllegalAccessException  {
		assertNotNull(qp.newInstance().createProfile(null));
	}
	
	@Parameters(name = "{index}: Check for language [{0}]")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ PliQualityProfile.class }, { CobolQualityProfile.class }, { AssemblerQualityProfile.class } ,
		};
		return Arrays.asList(data);
	}
}
