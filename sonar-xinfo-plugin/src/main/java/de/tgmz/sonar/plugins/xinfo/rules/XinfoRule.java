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

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.rule.RuleKey;

public abstract class XinfoRule {
	public void execute(SensorContext sensorContext, InputFile file, RuleKey ruleKey, String desc, int line) {
		NewIssue newIssue = sensorContext.newIssue();
		newIssue
			.forRule(ruleKey)
			.at(newIssue.newLocation()
					.on(file)
					.at(file.selectLine(line))
					.message(desc))
			.save();
	}
}
