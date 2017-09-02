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
package de.tgmz.sonar.plugins.xinfo.languages;

import java.util.Iterator;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import de.tgmz.sonar.plugins.xinfo.RuleFactory;
import de.tgmz.sonar.plugins.xinfo.SonarRule;

/**
 * Default quality profile for the projects having files of a supported language.
 */
public abstract class AbstractXinfoQualityProfile extends ProfileDefinition {
	private static final Logger LOGGER = Loggers.get(AbstractXinfoQualityProfile.class);
	
	private Language lang;

	public AbstractXinfoQualityProfile(Language lang) {
		super();
		this.lang = lang;
	}

	@Override
	public RulesProfile createProfile(ValidationMessages validation) {
		RulesProfile profile = RulesProfile.create("Xinfo Rules", lang.getKey());

		Iterator<SonarRule> it = RuleFactory.getInstance().getRules(lang).getRules().iterator();

		while (it.hasNext()) {
			String s = it.next().getKey();
				
			LOGGER.debug("Activate rule {}", s);
				
			profile.activateRule(Rule.create(lang.getRepoKey(), s), null);
		}

		return profile;
	}
}
