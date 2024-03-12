/*******************************************************************************
  * Copyright (c) 11.03.2024 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.languages;

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;

/**
 * This class defines the Xinfo language.
 */
public final class XinfoLanguage extends AbstractLanguage {

	public static final String NAME = "Xinfo";
	public static final String KEY = "xinfo";

	private final Configuration config;

	public XinfoLanguage(Configuration config) {
		super(KEY, NAME);
		this.config = config;
	}

	@Override
	public String[] getFileSuffixes() {
		return config.getStringArray(XinfoConfig.FILE_SUFFIXES_KEY);
	}
}
