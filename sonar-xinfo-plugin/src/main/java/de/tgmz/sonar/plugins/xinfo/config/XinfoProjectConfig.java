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

public class XinfoProjectConfig {
	public static final String COLORING_LIMIT = "sonar.xinfo.coloring.limit";
	public static final String XINFO_ROOT = "sonar.xinfo.root.xinfo";
	public static final String XINFO_CPD_OFF = "sonar.xinfo.cpd.off";
	public static final String XINFO_ENCODING = "sonar.xinfo.encoding";
	public static final String XINFO_LOG_THRESHOLD = "sonar.xinfo.log.threshold";
	public static final String XINFO_INCLUDE_LEVEL = "sonar.xinfo.include.levels";
	public static final String XINFO_NUM_THREADS = "sonar.xinfo.threads";
	public static final String CATEGORY = "Xinfo";

	private XinfoProjectConfig() {
		// Only statics
	}

	public static List<PropertyDefinition> definitions() {
		return asList(PropertyDefinition.builder(COLORING_LIMIT)
				.name("XinfoColoringLimit")
				.description("Xinfo Coloring Limit")
				.defaultValue(String.valueOf(5000))
				.category(CATEGORY)
				.build()
			, PropertyDefinition.builder(XINFO_ROOT)
				.name("RootXinfo")
				.description("XINFO root directory")
				.category(CATEGORY)
				.build()
			, PropertyDefinition.builder(XINFO_CPD_OFF)
				.name("XinfoCpdOff")
				.description("XINFO turn off copy/paste detection")
				.category(CATEGORY)
				.build()
			, PropertyDefinition.builder(XINFO_ENCODING)
				.name("EncodingXinfo")
				.description("XINFO encoding")
				.category(CATEGORY)
				.build()
			, PropertyDefinition.builder(XINFO_LOG_THRESHOLD)
				.name("LogThresholdXinfo")
				.description("XINFO log threshold")
				.category(CATEGORY)
				.build()
			, PropertyDefinition.builder(XINFO_INCLUDE_LEVEL)
				.name("IncludeLevelXinfo")
				.description("XINFO include level. Comma separated list of levels, e.g. \'E,S,U\'")
				.category(CATEGORY)
				.build()
			, PropertyDefinition.builder(XINFO_NUM_THREADS)
				.name("XinfoNumThreads")
				.description("Number of concurrent threads")
				.category(CATEGORY)
				.build()
		);
	}
}
