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
package de.tgmz.sonar.plugins.xinfo.languages;

import static de.tgmz.sonar.plugins.xinfo.rules.XinfoRuleDefinition.REPO_KEY;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.check.Rule;

import de.tgmz.sonar.plugins.xinfo.RuleFactory;

/**
 * Default, BuiltIn Quality Profile for the projects having files of the
 * language "xinfo"
 */
public final class XinfoQualityProfile implements BuiltInQualityProfilesDefinition {

	@Override
	public void define(Context context) {
		NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Xinfo way", XinfoLanguage.KEY);
		profile.setDefault(true);

		for (Class<?> c : RuleFactory.getInstance().getRules()) {
			profile.activateRule(REPO_KEY, c.getDeclaredAnnotation(Rule.class).key());
		}

		profile.done();
	}
}
