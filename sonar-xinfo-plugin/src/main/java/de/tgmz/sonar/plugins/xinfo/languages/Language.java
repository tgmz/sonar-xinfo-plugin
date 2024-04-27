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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.sonar.api.profiles.RulesProfile;

/**
 * Defines the supported languages.
 */
public enum Language {
	PLI("pli,pl1"),
	COBOL("cbl,cob"),
	ASSEMBLER("asm,assemble"),
	C("c"),
	CPP("cpp"),
	INCLUDE("include"),
	COPYBOOK("cpy"),
	MACRO("mac"),
	HEADER("h,hpp"),
	REXX("rexx"),
	BMS("bms")
	;
	
	private List<String> defaultFileSuffixes;
	
	/**
	 * CTOR.
	 * @param key the language key used by Sonars {@link RulesProfile}
	 * @param name the language name used by Sonars {@link RulesProfile}
	 * @param defaultFileSuffixes List of comma-separated file suffixes
	 */
	private Language(String defaultFileSuffixes) {
		this.defaultFileSuffixes = Arrays.asList(defaultFileSuffixes.split(","));
	}

	public static Language getByFilename(String f) {
		for (Language l : values()) {
			for (String ext : l.defaultFileSuffixes) {
				if (ext.equals(FilenameUtils.getExtension(f))) {
					return l;
				}
			}
		}

		throw new IllegalArgumentException("No language for file [" + f + "]");
	}

	public List<String> getDefaultFileSuffixes() {
		return defaultFileSuffixes;
	}
	
	public static String getAllFileSuffixes() {
		StringBuilder sb = new StringBuilder();
		
		for (Language lang : Language.values()) {
			for (String s : lang.defaultFileSuffixes) {
				sb.append(s);
				sb.append(",");
			}
		}
		
		return sb.substring(0, sb.length() - 1);
	}
	
	public boolean canCompile() {
		return this == ASSEMBLER || this == COBOL || this == PLI || this == C || this == CPP;
	}
	
	public boolean isMacro() {
		return this == COPYBOOK || this == INCLUDE || this == MACRO || this == HEADER;
	}
	
	public boolean isMask() {
		return this == BMS;
	}
}
