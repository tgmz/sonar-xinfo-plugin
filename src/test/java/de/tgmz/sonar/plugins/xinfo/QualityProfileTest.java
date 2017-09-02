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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.tgmz.sonar.plugins.xinfo.languages.AssemblerQualityProfile;
import de.tgmz.sonar.plugins.xinfo.languages.CobolQualityProfile;
import de.tgmz.sonar.plugins.xinfo.languages.PliQualityProfile;

/**
 * Simple testcases for QualityProfile.
 */
public class QualityProfileTest {

	@Test
	public void testPli()  {
		assertNotNull(new PliQualityProfile().createProfile(null));
	}

	@Test
	public void testCobol()  {
		assertNotNull(new CobolQualityProfile().createProfile(null));
	}

	@Test
	public void testAssembler()  {
		assertNotNull(new AssemblerQualityProfile().createProfile(null));
	}
}
