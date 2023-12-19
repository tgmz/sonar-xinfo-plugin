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

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

public class XinfoMojoUnsupportedTest extends AbstractXinfoMojoTest {
	@Test(expected = MojoExecutionException.class)
	public void testGenerate() throws Exception {
		myMojo.setLang("fortran");
		myMojo.execute();
	}
}
