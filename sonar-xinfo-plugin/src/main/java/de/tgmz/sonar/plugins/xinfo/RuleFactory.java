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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.sonar.plugins.xinfo.rules.XinfoRule;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

/**
 * Factory for loading the xinfo sonar rules.
 */
public final class RuleFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(RuleFactory.class);
	private static final String PKG = "de.tgmz.sonar.plugins.xinfo.rules.generated";
	
	private static RuleFactory instance;
	private List<Class<?>> rules;

	private RuleFactory() {
		long start = System.currentTimeMillis();

		try (ScanResult scanResult = new ClassGraph().enableClassInfo().acceptPackages(PKG).scan()) {
			ClassInfoList xinfoRuleClasses = scanResult.getSubclasses(XinfoRule.class);
			rules = xinfoRuleClasses.loadClasses();
		}
		
		LOGGER.info("Loaded {} rules in {} msec", rules.size(), System.currentTimeMillis() - start);
	}

	public static synchronized RuleFactory getInstance() {
		if (instance == null) {
			LOGGER.debug("Create new Factory instance");
			
			instance = new RuleFactory();
		}

		return instance;
	}

	public List<Class<?>> getRules() {
		return rules;
	}
}
