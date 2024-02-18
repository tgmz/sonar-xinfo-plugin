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
package de.tgmz.sonar.plugins.xinfo;

import java.util.List;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.check.Rule;

import de.tgmz.sonar.plugins.xinfo.languages.Language;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

/**
 * Factory for creating the sonar rules for a {@link Language}
 */
public final class RuleFactory {
	private static final Logger LOGGER = Loggers.get(RuleFactory.class);
	private static RuleFactory instance;

	private RuleFactory() {
	}

	public static synchronized RuleFactory getInstance() {
		if (instance == null) {
			LOGGER.debug("Create new Factory instance");
			
			instance = new RuleFactory();
		}

		return instance;
	}

	public List<Class<?>> getRules(Language l) {
		String pkg = "de.tgmz.sonar.plugins.xinfo.rules.generated." + l.getKey();
		
		LOGGER.info("Get rules for {} from package {}", l, pkg);

		try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(pkg).scan()) {
			ClassInfoList xinfoRuleClasses = scanResult.getClassesWithAnnotation(Rule.class);
			return xinfoRuleClasses.loadClasses();
		}
	}
}
