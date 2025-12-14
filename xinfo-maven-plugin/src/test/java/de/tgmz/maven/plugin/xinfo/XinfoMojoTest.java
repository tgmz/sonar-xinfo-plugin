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
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class XinfoMojoTest extends AbstractXinfoMojoTest {
	private static final String[] EXPECTED_PLI = new String[] { "IBM1039I", "IBM1479I", "IBM3988I", "IBM1247I", "IBM2848I", "IBM1063I", "IBM1316I", "IBM2811I", "IBM2804I", "IBM2843I", "IBM5141" };
	private static final String[] EXPECTED_CCPP = new String[] { "CCN0000", "CLB9906" };		// First and last
	private static final String[] EXPECTED_COBOL = new String[] { "IGYXX0001", "IGYXX8400" };	// First and last
	private static final String[] EXPECTED_ASM = new String[] { "ASMA001", "ASMACMS076" };		// First and last
	
	private static final Map<String, String[]> EXP 
	= Map.of("pli", EXPECTED_PLI
			, "ccpp", EXPECTED_CCPP
			, "asm", EXPECTED_ASM
			, "cobol", EXPECTED_COBOL);

	private String lang;
	private String doc;
	private String sysuexit;
	
	public XinfoMojoTest(String lang, String doc, String sysuexit) {
		super();
		this.lang = lang;
		this.doc = doc;
		this.sysuexit = sysuexit;
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
		myMojo.setSysuexit(sysuexit != null ? new File(sysuexit) : null);
		myMojo.execute();
	}
	
	@After
	public void checkRuleIsWritten() throws IOException {
		Path dir = Paths.get(myMojo.getOutputDirectory().getCanonicalPath(), myMojo.getTargetPackage().replace('.', File.separatorChar));

		String[] exp = EXP.get(lang);
		
		assertTrue(exp.length > 0);
		
		for (String rule : exp) {
			assertTrue(String.format("Rule %s not found",  rule), new File(dir.toFile(), rule + ".java").isFile());
		}
	}
	
	@AfterClass
	public static void teardownOnce() {
		File pom = new File("target/test-classes/project-to-test/");
		assertTrue(pom.exists());
	}
	
	@Parameters(name = "{index}: Check for language [{0}]")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ "cobol", "../ibm/cobol/ErrMsg.txt", null },
				{ "asm", "../ibm/Assembler/asmp1021.pdf", null },
				{ "ccpp", "../ibm/ccpp/cbcdg01_v3r1.pdf", null },
				{ "pli", "../ibm/pli/Messages and Codes.pdf", "src/test/resources/SYSUEXIT" },
				{ "pli", "../ibm/pli/Messages and Codes.pdf", "src/test/resources" },
		};
		return Arrays.asList(data);
	}
}
