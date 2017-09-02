/*******************************************************************************
  * Copyright (c) 02.12.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * File implementation of {@link XinfoFileAnalyzable}
 */
public class XinfoFileAnalyzable implements IXinfoAnalyzable {
	private File f;
	private Language language;

	public XinfoFileAnalyzable(Language language, File f) {
		this.language = language;
		this.f = f;
	}

	@Override
	public String getName() {
		int i = f.getName().lastIndexOf('.');
		
		return (i == -1 ? f.getName() : f.getName().substring(0, i)).toUpperCase(Locale.US);
	}

	@Override
	public String getSource() throws IOException {
		return IOUtils.toString(new FileInputStream(f), Charset.defaultCharset());
	}

	@Override
	public Language getLanguage() {
		return language;
	}
}
