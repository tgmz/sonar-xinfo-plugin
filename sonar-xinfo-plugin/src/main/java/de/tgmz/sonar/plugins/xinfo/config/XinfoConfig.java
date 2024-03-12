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
import org.sonar.api.resources.Qualifiers;

public class XinfoConfig {
	public static final String CATEGORY = "Xinfo";

	public static final String FILE_SUFFIXES_KEY = "sonar.xinfo.file.suffixes";
	public static final String FILE_SUFFIXES_DEFAULT_VALUE = ".pli,.asm,.cbl,.c,.cpp";

	private XinfoConfig() {
		// only statics
	}

	public static List<PropertyDefinition> definitions() {
		return asList(PropertyDefinition.builder(FILE_SUFFIXES_KEY)
				.multiValues(true)
				.defaultValue(FILE_SUFFIXES_DEFAULT_VALUE)
				.category(CATEGORY)
				.name("File Suffixes")
				.description("List of suffixes for files to analyze.")
				.onQualifiers(Qualifiers.PROJECT)
				.build()
			);				
	}
}
