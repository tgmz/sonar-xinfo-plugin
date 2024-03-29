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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.internal.ConfigurationBridge;
import org.sonar.api.config.internal.MapSettings;

/**
 * Testcase with dummy DocumentBuilderFactory to force {@link XinfoRuntimeException}.
 */
public class ParserConfigurationProblemTest {
	private final static String DOC_BUILDER_PROPERTY_NAME = "javax.xml.parsers.DocumentBuilderFactory";
	private static final Configuration CONFIGURATION = new ConfigurationBridge(new MapSettings());

	@Test(expected=XinfoRuntimeException.class)
	public void testParserConfigurationProblem() throws XinfoException {
		XinfoProviderFactory.getProvider(CONFIGURATION).getXinfo(null);	
	}

	@BeforeClass
	public static void setupDownOnce() {
		System.setProperty(DOC_BUILDER_PROPERTY_NAME, TestDocumentBuilderFactory.class.getCanonicalName());
	}
	
	@AfterClass
	public static void tearDownOnce() {
		System.clearProperty(DOC_BUILDER_PROPERTY_NAME);
	}
}
