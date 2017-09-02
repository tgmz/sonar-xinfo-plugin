/*******************************************************************************
  * Copyright (c) 29.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.settings;

import static java.util.Arrays.asList;

import java.util.List;

import org.sonar.api.config.PropertyDefinition;

/**
 * Runtime settings.
 */
public final class XinfoSettings {
	public static final String COLORIZING_LIMIT = "sonar.xinfo.colorizing.limit";
	public static final String ROOT_XINFO = "sonar.xinfo.root.xinfo";
	public static final String CATEGORY = "Xinfo";

	private XinfoSettings() {
		// Only statics
	}

	public static List<PropertyDefinition> definitions() {
		return asList(PropertyDefinition.builder(COLORIZING_LIMIT)
				.name("XinfoColorizingLimit")
				.description("Xinfo Colorizing Limit")
				.defaultValue(String.valueOf(5000))
				.category(CATEGORY).build()
			, PropertyDefinition.builder(ROOT_XINFO)
				.name("RootXinfo")
				.description("XINFO root directory")
				.category(CATEGORY).build()
			);
	}
}
