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

import org.junit.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

import de.tgmz.sonar.plugins.xinfo.languages.XinfoQualityProfile;

/**
 * Simple testcases for QualityProfile.
 */
public class QualityProfileTest {
	@Test(expected = Test.None.class)
	public void test() {
		new XinfoQualityProfile().define(new BuiltInQualityProfilesDefinition.Context());
	}
}
