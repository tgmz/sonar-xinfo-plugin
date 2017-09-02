/*******************************************************************************
  * Copyright (c) 13.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.rules;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Defines rules for all language.
 */
public final class XinfoRulesDefinition implements RulesDefinition {
	private static final Logger LOGGER = Loggers.get(XinfoRulesDefinition.class);

	@Override
	public void define(Context context) {
		for (Language lang : Language.values()) {
			defineRulesForLanguage(context, lang);
		}
	}

	private void defineRulesForLanguage(Context context, Language lang) {
		LOGGER.info("Loading Context {} for language {}", String.valueOf(context), lang.toString());
		
		NewRepository repository = context.createRepository(lang.getRepoKey(), lang.getKey()).setName(lang.getRepoName());

		try (InputStream rulesXml = this.getClass().getClassLoader().getResourceAsStream(lang.getRulesDefinition())) {
			if (rulesXml != null) {
				RulesDefinitionXmlLoader rulesLoader = new RulesDefinitionXmlLoader();
				rulesLoader.load(repository, rulesXml, StandardCharsets.UTF_8.name());
			} else {
				LOGGER.error("Cannot find rules definition file {}", lang.getRulesDefinition());
			}
		} catch (IOException e) {
			LOGGER.error("Error loading rules for " + lang.toString(), e);
		}

		repository.done();
	}
}
