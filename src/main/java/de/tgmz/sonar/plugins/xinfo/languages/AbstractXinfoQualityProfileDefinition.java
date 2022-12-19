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

import java.util.Iterator;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import de.tgmz.sonar.plugins.xinfo.RuleFactory;
import de.tgmz.sonar.plugins.xinfo.generated.Rule;

/**
 * Default quality profile for the projects having files of a supported language.
 */
public abstract class AbstractXinfoQualityProfileDefinition implements BuiltInQualityProfilesDefinition {
	private static final Logger LOGGER = Loggers.get(AbstractXinfoQualityProfileDefinition.class);
	
	private Language lang;

	protected AbstractXinfoQualityProfileDefinition(Language lang) {
		super();
		this.lang = lang;
	}

	@Override
	public void define(Context context) {
	    NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Xinfo Rules", lang.getKey());
	    profile.setDefault(false);
	    
		Iterator<Rule> it = RuleFactory.getInstance().getRules(lang).getRule().iterator();

		while (it.hasNext()) {
			String s = it.next().getKey();
				
			LOGGER.debug("Activate rule {}", s);
			
		    profile.activateRule(lang.getRepoKey(), s);
		}
	    
	    profile.done();
	}
}
