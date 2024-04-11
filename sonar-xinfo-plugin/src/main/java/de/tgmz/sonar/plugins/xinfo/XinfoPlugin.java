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
package de.tgmz.sonar.plugins.xinfo;

import org.sonar.api.Plugin;

import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;
import de.tgmz.sonar.plugins.xinfo.languages.XinfoLanguage;
import de.tgmz.sonar.plugins.xinfo.languages.XinfoQualityProfile;
import de.tgmz.sonar.plugins.xinfo.measures.ComputeDynamicComplexityAverage;
import de.tgmz.sonar.plugins.xinfo.measures.ComputeStaticComplexityAverage;
import de.tgmz.sonar.plugins.xinfo.measures.XinfoMetrics;
import de.tgmz.sonar.plugins.xinfo.rules.XinfoRuleDefinition;
import de.tgmz.sonar.plugins.xinfo.sensors.ColoringSensor;
import de.tgmz.sonar.plugins.xinfo.sensors.OtfSetupSensor;
import de.tgmz.sonar.plugins.xinfo.sensors.XinfoCpdSensor;
import de.tgmz.sonar.plugins.xinfo.sensors.XinfoIssuesLoader;

/**
 * This class is the entry point for all extensions. It is referenced in
 * pom.xml.
 */
public class XinfoPlugin implements Plugin {

	@Override
	public void define(Context context) {
		// tutorial on hooks

		// tutorial on languages
		// https://docs.sonarqube.org/9.4/extend/new-languages/
		context.addExtensions(XinfoLanguage.class, XinfoQualityProfile.class);
		context.addExtensions(XinfoConfig.definitions());

	    // tutorial on measures
	    context.addExtensions(XinfoMetrics.class, ComputeStaticComplexityAverage.class, ComputeDynamicComplexityAverage.class);

		// tutorial on rules
		context.addExtensions(XinfoRuleDefinition.class, XinfoIssuesLoader.class);
		
		context.addExtension(ColoringSensor.class);
		context.addExtension(XinfoCpdSensor.class);

		context.addExtension(OtfSetupSensor.class);

		// tutorial on web extensions
	}
}
