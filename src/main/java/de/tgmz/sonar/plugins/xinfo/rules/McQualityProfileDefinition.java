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

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * Default quality profile for the projects having files of a supported language.
 */
public class McQualityProfileDefinition implements BuiltInQualityProfilesDefinition {
	private static final Logger LOGGER = Loggers.get(McQualityProfileDefinition.class);
	
	public McQualityProfileDefinition() {
		super();
	}

	@Override
	public void define(Context context) {
	    NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("MC Rules for PL/I", "pli");
	    profile.setDefault(true);
	    
	    profile.activateRule("mc-pli", "MC00042");
	    
	    profile.done();
	}
}
