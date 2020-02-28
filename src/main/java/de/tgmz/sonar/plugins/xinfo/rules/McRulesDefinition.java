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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * Defines rules for all language.
 */
public final class McRulesDefinition implements RulesDefinition {
	private static final Logger LOGGER = Loggers.get(McRulesDefinition.class);

	@Override
	public void define(Context context) {
		NewRepository repository = context.createRepository("mc-pli", "pli").setName("MC PL/I");

		try (InputStream rulesXml = this.getClass().getClassLoader().getResourceAsStream("mc-rules.xml")) {
			if (rulesXml != null) {
				RulesDefinitionXmlLoader rulesLoader = new RulesDefinitionXmlLoader();
				rulesLoader.load(repository, rulesXml, StandardCharsets.UTF_8.name());
			} else {
				LOGGER.error("Cannot find rules definition file mc-rules");
			}
		} catch (IOException e) {
			LOGGER.error("Error loading rules for mc-rules.xml");
		}

		repository.done();
	}
}
