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
import org.sonar.api.server.rule.RulesDefinition.NewRepository;

/**
 * Defines the supported languages.
 */
public enum Language {
	PLI("pli", "PL/I", "pli,pl1", "xinfo-pli", "Xinfo PL/I"),
	COBOL("cbl", "Cobol", "cbl,cob", "xinfo-cbl", "Xinfo COBOL"),
	ASSEMBLER("asm", "Assembler", "asm", "xinfo-asm", "Xinfo Assembler"),
	C("c", "C", "c", "xinfo-c", "Xinfo C", "ccpp"),
	CPP("cpp", "C++", "cpp", "xinfo-cpp", "Xinfo C++", "ccpp"),
	;
	
	private String key;
	private String name;
	private List<String> defaultFileSuffixes;
	private String repoKey;
	private String repoName;
	private String ruleKey;
	
	/**
	 * CTOR.
	 * @param key the language key used by Sonars {@link RulesProfile}
	 * @param name the language name used by Sonars {@link RulesProfile}
	 * @param defaultFileSuffixes List of comma-separated file suffixes
	 * @param repoKey key for the {@link NewRepository}
	 * @param repoName name for the {@link NewRepository}
	 * @param ruleKey name for the package to load rules from. Defaults to key}
	 */
	private Language(String key, String name, String defaultFileSuffixes, String repoKey, String repoName, String ruleKey) {
		this.key = key;
		this.name = name;
		this.defaultFileSuffixes = Arrays.asList(defaultFileSuffixes.split(","));
		this.repoKey = repoKey;
		this.repoName = repoName;
		this.ruleKey = ruleKey;
	}

	private Language(String key, String name, String defaultFileSuffixes, String repoKey, String repoName) {
		this.key = key;
		this.name = name;
		this.defaultFileSuffixes = Arrays.asList(defaultFileSuffixes.split(","));
		this.repoKey = repoKey;
		this.repoName = repoName;
		this.ruleKey = key;
	}
	
	public static Language getByKey(String key) {
		for (Language l : values()) {
			if (key.equals(l.key)) {
				return l;
			}
		}

		throw new IllegalArgumentException("No language for key [" + key + "]");
	}

	public static Language getByExtension(String f) {
		for (Language l : values()) {
			for (String ext : l.defaultFileSuffixes) {
				if (ext.equals(FilenameUtils.getExtension(f))) {
					return l;
				}
			}
		}

		throw new IllegalArgumentException("No language for file [" + f + "]");
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public List<String> getDefaultFileSuffixes() {
		return defaultFileSuffixes;
	}

	public String getRepoKey() {
		return repoKey;
	}

	public String getRepoName() {
		return repoName;
	}

	public String getRuleKey() {
		return ruleKey;
	}
}
