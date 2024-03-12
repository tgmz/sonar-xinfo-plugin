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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;

import de.tgmz.sonar.plugins.xinfo.languages.XinfoLanguage;

/**
 * Simple Utility for test file creation.
 */
public class SonarTestFileUtil {
	public static DefaultInputFile create(String location, String fileName) throws IOException {
		return new TestInputFileBuilder(location, fileName)
						.setCharset(StandardCharsets.UTF_8)
						.setLanguage(XinfoLanguage.KEY)
						.initMetadata(IOUtils.toString(new FileInputStream(new File(location, fileName)), StandardCharsets.UTF_8))
						.build();
	}
}
