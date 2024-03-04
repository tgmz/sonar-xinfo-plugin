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

import java.util.List;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

import de.tgmz.sonar.plugins.xinfo.RuleFactory;

/**
 * Default quality profile for the projects having files of a supported language.
 */
public abstract class AbstractXinfoQualityProfileDefinition implements BuiltInQualityProfilesDefinition {
	private List<Language> languages;

	protected AbstractXinfoQualityProfileDefinition(List<Language> languages) {
		super();
		this.languages = languages;
	}

	@Override
	public void define(Context context) {
		for (Language lang : languages) {
			NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Xinfo Rules", lang.getKey());
			profile.setDefault(false);
	    
			RuleFactory.getInstance().getRules(lang).forEach(s -> profile.activateRule(lang.getRepoKey(), s.getSimpleName()));
	    
			profile.done();
		}
	}
}
