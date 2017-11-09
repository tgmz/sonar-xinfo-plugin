/*******************************************************************************
  * Copyright (c) 02.12.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.sonar.api.batch.fs.InputFile;

import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * File implementation of {@link XinfoFileAnalyzable}
 */
public class XinfoFileAnalyzable implements IXinfoAnalyzable {
	private InputFile f;
	private Language language;

	public XinfoFileAnalyzable(Language language, InputFile f) {
		this.language = language;
		this.f = f;
	}

	@Override
	public String getName() {
		int i = f.filename().lastIndexOf('.');
		
		return (i == -1 ? f.filename() : f.filename().substring(0, i));
	}

	@Override
	public String getSource() throws IOException {
		return IOUtils.toString(f.inputStream(), Charset.defaultCharset());
	}

	@Override
	public Language getLanguage() {
		return language;
	}
}
