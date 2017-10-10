/*******************************************************************************
  * Copyright (c) 13.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.languages;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.resources.AbstractLanguage;

/**
 * This class acts as a bridge between the {@link AbstractLanguage} used by Sonar and the {@link Language} enum.
 */
public class LanguageBridge extends AbstractLanguage {
	public LanguageBridge(String key, String name) {
		super(key, name);
	}

	@Override
	public String[] getFileSuffixes() {
		return StringUtils.split(Language.getByKey(getKey()).getDefaultFileSuffixes(), ",");
	}
}
