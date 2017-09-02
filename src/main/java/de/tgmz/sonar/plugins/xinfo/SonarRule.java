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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Java representation of a Sonar rule.
 */
@XmlRootElement(name="rule")
public class SonarRule {
	private String key;
	private String name;
	private String internalKey;
	private String description;
	private String severity;
	private String cardinality;
	private String status;
	private List<String> tag;
	private String type;
	private String remediationFunction;
	private String remediationFunctionBaseEffort;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInternalKey() {
		return internalKey;
	}
	public void setInternalKey(String internalKey) {
		this.internalKey = internalKey;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getCardinality() {
		return cardinality;
	}
	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<String> getTag() {
		return tag;
	}
	public void setTag(List<String> tag) {
		this.tag = tag;
	}
	public String getRemediationFunction() {
		return remediationFunction;
	}
	public void setRemediationFunction(String remediationFunction) {
		this.remediationFunction = remediationFunction;
	}
	public String getRemediationFunctionBaseEffort() {
		return remediationFunctionBaseEffort;
	}
	public void setRemediationFunctionBaseEffort(String remediationFunctionBaseEffort) {
		this.remediationFunctionBaseEffort = remediationFunctionBaseEffort;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
