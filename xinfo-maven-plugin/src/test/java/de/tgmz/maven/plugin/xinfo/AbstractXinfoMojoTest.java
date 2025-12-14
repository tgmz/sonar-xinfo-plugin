/*******************************************************************************
  * Copyright (c) 19.12.2023 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.maven.plugin.xinfo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;

public abstract class AbstractXinfoMojoTest {
	static XinfoMojo myMojo;
	@ClassRule
	public static MojoRule rule = new MojoRule() {
		@Override
		protected void before() throws Throwable {
			// Do nothing
		}

		@Override
		protected void after() {
			// Do nothing
		}
	};
	
	@BeforeClass
	public static void setupOnce() throws Exception {
		File pom = new File("target/test-classes/project-to-test/");
		assertTrue(pom.exists());

		myMojo = (XinfoMojo) rule.lookupConfiguredMojo(pom, "generate");
		assertNotNull(myMojo);
	}
}
