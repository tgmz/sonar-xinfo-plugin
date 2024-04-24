/*******************************************************************************
  * Copyright (c) 04.03.2024 Thomas Zierer.
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
 * Runtime config for on-the-fly processing.
 */
public final class XinfoFtpConfig {
	public static final String XINFO_OTF = "sonar.xinfo.otf";
	public static final String XINFO_OTF_JOBCARD = "sonar.xinfo.otf.jobcard";
	public static final String XINFO_OTF_SERVER = "sonar.xinfo.otf.server";
	public static final String XINFO_OTF_PORT = "sonar.xinfo.otf.port";
	public static final String XINFO_OTF_USER = "sonar.xinfo.otf.user";
	public static final String XINFO_OTF_PASS = "sonar.xinfo.otf.pass";
	public static final String XINFO_OTF_TIMEOUT = "sonar.xinfo.otf.timeout";
	public static final String XINFO_OTF_SYSLIB = "sonar.xinfo.otf.syslib";
	public static final String CATEGORY = "Xinfo";

	private XinfoFtpConfig() {
		// Only statics
	}

	public static List<PropertyDefinition> definitions() {
		return asList(PropertyDefinition.builder(XINFO_OTF)
				.name("XinfoOtf")
				.description("XINFO on-the-fly")
				.category(CATEGORY)
				.build()
			, PropertyDefinition.builder(XINFO_OTF_JOBCARD)
				.name("OtfJobCard")
				.description("XINFO on-the-fly jobcard")
				.category(CATEGORY)
				.build()
			, PropertyDefinition.builder(XINFO_OTF_PASS)
				.name("OtfPass")
				.description("XINFO on-the-fly password")
				.category(CATEGORY)
				.build()
			, PropertyDefinition.builder(XINFO_OTF_SERVER)
				.name("OtfServer")
				.description("XINFO on-the-fly server")
				.category(CATEGORY).build()
			, PropertyDefinition.builder(XINFO_OTF_PORT)
				.name("OtfServerPort")
				.description("XINFO on-the-fly server port")
				.defaultValue("21")
				.category(CATEGORY)
				.build()
			, PropertyDefinition.builder(XINFO_OTF_USER)
				.name("OtfUser")
				.description("XINFO on-the-fly user")
				.category(CATEGORY)
				.build()
			, PropertyDefinition.builder(XINFO_OTF_TIMEOUT)
				.name("OtfTimeout")
				.description("XINFO timeout to wait for the job to finish in seconds")
				.defaultValue("10")
				.category(CATEGORY)
				.build()
			, PropertyDefinition.builder(XINFO_OTF_SYSLIB)
				.name("OtfSyslib")
				.description("XINFO SYSLIB")
				.category(CATEGORY)
				.build()
		);
	}
}
