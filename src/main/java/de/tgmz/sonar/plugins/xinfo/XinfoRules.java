/*******************************************************************************
  * Copyright (c) 17.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Java representation of a rule-list.xml.
 */
@XmlRootElement(name = "xinfo-rules")
public class XinfoRules {
	private List<SonarRule> rules;

	@XmlElement(name = "rule")
	public List<SonarRule> getRules() {
		return rules;
	}

	public void setRules(List<SonarRule> rules) {
		this.rules = rules;
	}
}
