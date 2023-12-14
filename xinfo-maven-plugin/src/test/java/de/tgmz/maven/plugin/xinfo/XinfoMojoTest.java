/*******************************************************************************
  * Copyright (c) 14.12.2016 Thomas Zierer.
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
import java.util.Arrays;
import java.util.Collection;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class XinfoMojoTest {
	private static XinfoMojo myMojo;
	private String lang;
	private String doc;
	@ClassRule
	public static MojoRule rule = new MojoRule() {
		@Override
		protected void before() throws Throwable {
		}

		@Override
		protected void after() {
		}
	};
	
	public XinfoMojoTest(String lang, String doc) {
		super();
		this.lang = lang;
		this.doc = doc;
	}
	
	@BeforeClass
	public static void setupOnce() throws Exception {
		File pom = new File("target/test-classes/project-to-test/");
		assertTrue(pom.exists());

		myMojo = (XinfoMojo) rule.lookupConfiguredMojo(pom, "generate");
		assertNotNull(myMojo);
	}

	@Test(expected = None.class)
	public void testGenerate() throws Exception {
		myMojo.setDocument(new File(doc));
		myMojo.setLang(lang);
		myMojo.execute();
	}
	
	@Parameters(name = "{index}: Check for language [{0}]")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ "asm", "ibm/Assembler/asmp1021.pdf" },
				{ "cobol", "ibm/cobol/ErrMsg.txt" },
				{ "ccpp", "ibm/ccpp/cbcdg01_v2r4.pdf" },
				{ "pli", "ibm/pli/Messages and Codes.pdf" },
		};
		return Arrays.asList(data);
	}
}
