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
package de.tgmz.sonar.plugins.xinfo;

import org.sonar.api.Plugin;

import de.tgmz.sonar.plugins.xinfo.languages.AssemblerLanguage;
import de.tgmz.sonar.plugins.xinfo.languages.AssemblerQualityProfileDefinition;
import de.tgmz.sonar.plugins.xinfo.languages.CCPPLanguage;
import de.tgmz.sonar.plugins.xinfo.languages.CCPPQualityProfileDefinition;
import de.tgmz.sonar.plugins.xinfo.languages.CobolLanguage;
import de.tgmz.sonar.plugins.xinfo.languages.CobolQualityProfileDefinition;
import de.tgmz.sonar.plugins.xinfo.languages.PliLanguage;
import de.tgmz.sonar.plugins.xinfo.languages.PliQualityProfileDefinition;
import de.tgmz.sonar.plugins.xinfo.rules.XinfoRulesDefinition;
import de.tgmz.sonar.plugins.xinfo.sensors.AssemblerColorizer;
import de.tgmz.sonar.plugins.xinfo.sensors.AssemblerIssuesLoader;
import de.tgmz.sonar.plugins.xinfo.sensors.CCPPColorizer;
import de.tgmz.sonar.plugins.xinfo.sensors.CCPPIssuesLoader;
import de.tgmz.sonar.plugins.xinfo.sensors.CobolColorizer;
import de.tgmz.sonar.plugins.xinfo.sensors.CobolCpdSensor;
import de.tgmz.sonar.plugins.xinfo.sensors.CobolIssuesLoader;
import de.tgmz.sonar.plugins.xinfo.sensors.DefaultCpdSensor;
import de.tgmz.sonar.plugins.xinfo.sensors.PliColorizer;
import de.tgmz.sonar.plugins.xinfo.sensors.PliIssuesLoader;

/**
 * This class is the entry point for all extensions. It is referenced in pom.xml.
 */
public class XinfoPlugin implements Plugin {

  @Override
  public void define(Context context) {
    // tutorial on hooks
    // http://docs.sonarqube.org/display/DEV/Adding+Hooks

    // tutorial on languages
    context.addExtensions(PliLanguage.class, PliQualityProfileDefinition.class);
    context.addExtensions(CobolLanguage.class, CobolQualityProfileDefinition.class);
    context.addExtensions(AssemblerLanguage.class, AssemblerQualityProfileDefinition.class);
    context.addExtensions(CCPPLanguage.class, CCPPQualityProfileDefinition.class);

    // tutorial on measures

    // tutorial on rules
    context.addExtension(XinfoRulesDefinition.class);

    context.addExtensions(PliIssuesLoader.class, CobolIssuesLoader.class, AssemblerIssuesLoader.class, CCPPIssuesLoader.class);

    context.addExtensions(PliColorizer.class, CobolColorizer.class, AssemblerColorizer.class, CCPPColorizer.class);

    context.addExtensions(DefaultCpdSensor.class, CobolCpdSensor.class);
    // tutorial on settings

    // tutorial on web extensions
  }
}
