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
package de.tgmz.sonar.plugins.xinfo.rules;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.sonar.plugins.xinfo.RuleFactory;
import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Defines rules for all language.
 */
public final class XinfoRulesDefinition implements RulesDefinition {
	private static final Logger LOGGER = LoggerFactory.getLogger(XinfoRulesDefinition.class);

	@Override
	public void define(Context context) {
		for (Language lang : Language.values()) {
			defineRulesForLanguage(context, lang);
		}
	}

	private void defineRulesForLanguage(Context context, Language lang) {
		LOGGER.info("Loading context for language {}", lang);
		
		NewRepository repository = context.createRepository(lang.getRepoKey(), lang.getKey()).setName(lang.getRepoName());

		RulesDefinitionAnnotationLoader rulesLoader = new RulesDefinitionAnnotationLoader();

		RuleFactory.getInstance().getRules(lang).forEach(s -> rulesLoader.load(repository, s));

		repository.done();
	}
}
