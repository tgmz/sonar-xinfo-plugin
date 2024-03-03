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
	public static final String XINFO_LOG_THRESHOLD = "sonar.xinfo.log.threshold";
	public static final String XINFO_INCLUDE_LEVEL = "sonar.xinfo.include.levels";
	public static final String XINFO_OTF = "sonar.xinfo.otf";
	public static final String XINFO_OTF_JOBCARD = "sonar.xinfo.otf.jobcard";
	public static final String XINFO_OTF_SERVER = "sonar.xinfo.otf.server";
	public static final String XINFO_OTF_PORT = "sonar.xinfo.otf.port";
	public static final String XINFO_OTF_USER = "sonar.xinfo.otf.user";
	public static final String XINFO_OTF_PASS = "sonar.xinfo.otf.pass";
	public static final String XINFO_OTF_TIMEOUT = "sonar.xinfo.otf.timeout";
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
			, PropertyDefinition.builder(XINFO_LOG_THRESHOLD)
				.name("LogThresholdXinfo")
				.description("XINFO log threshold")
				.category(CATEGORY).build()
			, PropertyDefinition.builder(XINFO_INCLUDE_LEVEL)
				.name("IncludeLevelXinfo")
				.description("XINFO include level. Comma separated list of levels, e.g. \'E,S,U\'")
				.category(CATEGORY).build()
			, PropertyDefinition.builder(XINFO_OTF)
				.name("XinfoOtf")
				.description("XINFO on-the-fly")
				.defaultValue("false")
				.category(CATEGORY).build()
			, PropertyDefinition.builder(XINFO_OTF_JOBCARD)
				.name("OtfJobCard")
				.description("XINFO on-the-fly jobcard")
				.category(CATEGORY).build()
			, PropertyDefinition.builder(XINFO_OTF_PASS)
				.name("OtfPass")
				.description("XINFO on-the-fly password")
				.category(CATEGORY).build()
			, PropertyDefinition.builder(XINFO_OTF_SERVER)
				.name("OtfServer")
				.description("XINFO on-the-fly server")
				.category(CATEGORY).build()
			, PropertyDefinition.builder(XINFO_OTF_PORT)
				.name("OtfTimeout")
				.description("XINFO on-the-fly server port")
				.defaultValue("21")
				.category(CATEGORY).build()
			, PropertyDefinition.builder(XINFO_OTF_USER)
				.name("OtfUser")
				.description("XINFO on-the-fly user")
				.category(CATEGORY).build()
			, PropertyDefinition.builder(XINFO_OTF_TIMEOUT)
				.name("OtfTimeout")
				.description("XINFO timeout to wait for the job to finish in seconds")
				.defaultValue("10")
				.category(CATEGORY).build()
		);
	}
}
