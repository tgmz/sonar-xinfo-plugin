/*******************************************************************************
  * Copyright (c) 03.03.2024 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/

package de.tgmz.sonar.plugins.xinfo.otf;
import java.io.IOException;

import org.junit.BeforeClass;

/**
 * Tests for all sensors.
 */
public class SensorOnTheFlyTest extends AbstractSensorOtfTest {
	@BeforeClass
	public static void setupOnce() throws IOException {
		setupEnvironment("zowe", 10443, 2, true);
	}
}
