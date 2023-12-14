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

import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;

/**
 * Testcase for XinfoPlugin.
 */
public class XinfoPluginTest {
	@Test
	public void testSettings() {
		SonarRuntime runtime = SonarRuntimeImpl.forSonarQube(Version.create(8, 9), SonarQubeSide.SCANNER, SonarEdition.COMMUNITY);
		Plugin.Context context = new Plugin.Context(runtime);
		new XinfoPlugin().define(context);
		assertTrue(context.getExtensions().size() > 0);
	}
}
