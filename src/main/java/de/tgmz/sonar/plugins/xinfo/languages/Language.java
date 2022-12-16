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

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;

/**
 * Defines the supported languages.
 */
public enum Language {
	PLI("pli", "PL/I", "pli,pl1", "pli-rules.xml", "xinfo-pli", "Xinfo PL/I"),
	COBOL("cbl", "Cobol", "cbl,cob", "cobol-rules.xml", "xinfo-cbl", "Xinfo COBOL"),
	ASSEMBLER("asm", "Assembler", "asm", "assembler-rules.xml", "xinfo-asm", "Xinfo Assembler"),
	;
	
	private String key;
	private String name;
	private List<String> defaultFileSuffixes;
	private String rulesDefinition;
	private String repoKey;
	private String repoName;
	
	/**
	 * CTOR.
	 * @param key the language key used by Sonars {@link RulesProfile}
	 * @param name the language name used by Sonars {@link RulesProfile}
	 * @param defaultFileSuffixes List of comma-separated file suffixes
	 * @param rulesDefinition name of the rules-definitions file
	 * @param repoKey key for the {@link NewRepository}
	 * @param repoName name for the {@link NewRepository}
	 */
	private Language(String key, String name, String defaultFileSuffixes, String rulesDefinition, String repoKey, String repoName) {
		this.key = key;
		this.name = name;
		this.defaultFileSuffixes = Arrays.asList(defaultFileSuffixes.split(","));
		this.rulesDefinition = rulesDefinition;
		this.repoKey = repoKey;
		this.repoName = repoName;
	}

	public static Language getByKey(String key) {
		for (Language l : values()) {
			if (key.equals(l.key)) {
				return l;
			}
		}

		throw new IllegalArgumentException("No language for key [" + key + "]");
	}

	public static Language getByExtension(File f) {
		for (Language l : values()) {
			for (String ext : l.defaultFileSuffixes) {
				if (ext.equals(FilenameUtils.getExtension(f.getName()))) {
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

	public String getRulesDefinition() {
		return rulesDefinition;
	}

	public String getRepoKey() {
		return repoKey;
	}

	public String getRepoName() {
		return repoName;
	}
}
