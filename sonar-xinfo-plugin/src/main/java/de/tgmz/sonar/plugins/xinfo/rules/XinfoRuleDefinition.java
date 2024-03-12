/*******************************************************************************
  * Copyright (c) 11.03.2024 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.rules;

import java.util.List;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;

import de.tgmz.sonar.plugins.xinfo.RuleFactory;
import de.tgmz.sonar.plugins.xinfo.languages.XinfoLanguage;

public final class XinfoRuleDefinition implements RulesDefinition {

	static final String KEY = "xinfo";
	public static final String REPO_KEY = XinfoLanguage.KEY + "-" + KEY;
	private static final String REPO_NAME = XinfoLanguage.KEY + "- " + KEY + " repo";

	@Override
	public void define(Context context) {
		NewRepository repository = context.createRepository(REPO_KEY, XinfoLanguage.KEY).setName(REPO_NAME);

		RulesDefinitionAnnotationLoader rulesDefinitionAnnotationLoader = new RulesDefinitionAnnotationLoader();
		
		List<Class<?>> rules = RuleFactory.getInstance().getRules();
		
		rulesDefinitionAnnotationLoader.load(repository, rules.toArray(new Class<?>[rules.size()]));

		repository.done();
	}
}
