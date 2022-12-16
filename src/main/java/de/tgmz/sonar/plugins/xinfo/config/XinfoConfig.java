/*******************************************************************************
  * Copyright (c) 29.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.config;

import static java.util.Arrays.asList;

import java.util.List;

import org.sonar.api.config.PropertyDefinition;

/**
 * Runtime config.
 */
public final class XinfoConfig {
	public static final String COLORIZING_LIMIT = "sonar.xinfo.colorizing.limit";
	public static final String XINFO_ROOT = "sonar.xinfo.root.xinfo";
	public static final String XINFO_CPD_OFF = "sonar.xinfo.cpd.off";
	public static final String XINFO_ENCODING = "sonar.xinfo.encoding";
	public static final String CATEGORY = "Xinfo";

	private XinfoConfig() {
		// Only statics
	}

	public static List<PropertyDefinition> definitions() {
		return asList(PropertyDefinition.builder(COLORIZING_LIMIT)
				.name("XinfoColorizingLimit")
				.description("Xinfo Colorizing Limit")
				.defaultValue(String.valueOf(5000))
				.category(CATEGORY).build()
			, PropertyDefinition.builder(XINFO_ROOT)
				.name("RootXinfo")
				.description("XINFO root directory")
				.category(CATEGORY).build()
			, PropertyDefinition.builder(XINFO_CPD_OFF)
				.name("XinfoCpdOff")
				.description("XINFO turn off copy/paste detection")
				.category(CATEGORY).build()
			, PropertyDefinition.builder(XINFO_ENCODING)
				.name("EncodingXinfo")
				.description("XINFO encoding")
				.category(CATEGORY).build()
		);
	}
}
