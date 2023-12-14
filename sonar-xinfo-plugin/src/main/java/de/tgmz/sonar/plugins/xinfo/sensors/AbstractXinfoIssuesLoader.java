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
package de.tgmz.sonar.plugins.xinfo.sensors;

import java.util.Iterator;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.issue.impact.Severity;
import org.sonar.api.issue.impact.SoftwareQuality;
import org.sonar.api.rule.RuleKey;

import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.XinfoProviderFactory;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.MESSAGE;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;
import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * This Sensor loads the results of an analysis performed by 
 * "real" mainframe compiler. Results are provided as an xml file
 * correspond to the rules defined in "&lt;language&gt;-rules.xml".
 */
public abstract class AbstractXinfoIssuesLoader implements Sensor {
	private static class Issue {
		InputFile inputFile;
		int line;
		String ruleKey;
		String message;		
	}
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractXinfoIssuesLoader.class);
	protected final FileSystem fileSystem;
	protected SensorContext context;
	private Language lang;
	
	protected AbstractXinfoIssuesLoader(final FileSystem fileSystem, Language lang) {
		this.fileSystem = fileSystem;
		this.lang = lang;
	}

	@Override
	public void describe(final SensorDescriptor descriptor) {
		descriptor.name(lang.getName() + " Issues Loader Sensor");
		descriptor.onlyOnLanguage(lang.getKey());
	}
	
	@Override
	public void execute(final SensorContext aContext) {
		LOGGER.debug("execute");

		this.context = aContext;
		
		Iterator<InputFile> fileIterator = fileSystem.inputFiles(fileSystem.predicates().hasLanguage(lang.getKey())).iterator();

		int ctr = 0;
		
		while (fileIterator.hasNext()) {
			InputFile inputFile = fileIterator.next();
			
			PACKAGE p;
			try {
				p = XinfoProviderFactory.getProvider(context.config()).getXinfo(inputFile);
			} catch (XinfoException e) {
				LOGGER.error("Error getting XINFO for file {}", inputFile, e);
				
				continue;
			}
				
			createFindings(p, inputFile);
			
			if (++ctr % 100 == 0) {
				LOGGER.info("{} files processed, current is {}", ctr, inputFile);
			}
		}
	}

	private void createFindings(PACKAGE p, InputFile file) {
		for (MESSAGE m : p.getMESSAGE()) {
			Issue issue = computeIssue(m, file);
				
			if (issue != null) {
				Severity severity = null;
					
				String ruleKey = issue.ruleKey;
					
				if (lang == Language.CCPP) {
					// The original rule key does not contain the severity. The severity is added in 
					// XinfoFileProvider.getXinfoFromEvent() and is processed here.
					switch (ruleKey.charAt(7)) {
					case 'I':
						severity = Severity.LOW;
						break;
					case 'W':
						severity = Severity.MEDIUM;
						break;
					case 'E':
					case 'S':
					case 'U':
					default:
						severity = Severity.HIGH;
						break;
					}
					
					ruleKey = ruleKey.substring(0, 7);
				}
				
				saveIssue(issue.inputFile, issue.line, ruleKey, issue.message, severity);
			}
		}
	}
	
	private Issue computeIssue(MESSAGE m, InputFile inputFile) {
		String msgFile = m.getMSGFILE();
		
		if (StringUtils.isEmpty(msgFile)) {
			return null;
		}
		
		Issue result = new Issue();
		
		result.message = m.getMSGTEXT();
		result.ruleKey = m.getMSGNUMBER();
		
		if (lang == Language.COBOL) {
			// Chars at 3 and 4 indicate the compile phase which issued the message
			// In ErrMsg these chars are replaced by "XX"
			result.ruleKey = result.ruleKey.substring(0, 3) + "XX" + result.ruleKey.substring(5, 9);	// remove severity
		}
			
		if (lang == Language.PLI) {
			result.ruleKey = result.ruleKey.substring(0, 8);	// remove severity
		}
			
		if (lang == Language.ASSEMBLER) {
			result.ruleKey = result.ruleKey.substring(0, 7);	// remove severity
		}
			
		if (XinfoUtil.isMainFile(msgFile, lang)) {
			result.line = Integer.parseInt(m.getMSGLINE());
			result.inputFile = inputFile;
		} else {
			result = null;
		}
		
		return result;
	}

	private void saveIssue(final InputFile inputFile, int line, String ruleKeyString, final String message, @Nullable Severity severity) {
		LOGGER.debug("Save issue {} for file {} on line {}", ruleKeyString, inputFile, line);
		
		RuleKey ruleKey = RuleKey.of(lang.getRepoKey(), ruleKeyString);

		NewIssue newIssue = context.newIssue().forRule(ruleKey);
		
		if (severity != null) {
			newIssue.overrideImpact(SoftwareQuality.RELIABILITY, severity);
		}
		
		NewIssueLocation primaryLocation = newIssue.newLocation().on(inputFile).message(message);
		
		int lineToSave = line;
		
		if (lineToSave > 0) {
			int maxLine = inputFile.lines();
			if (lineToSave > maxLine) {
				LOGGER.info("Linenumber {} for {} is outside range. It was reduced to {}", lineToSave, inputFile, maxLine);
				
				lineToSave = inputFile.lines();
			}
			
			primaryLocation.at(inputFile.selectLine(lineToSave));
		}
		
		newIssue.at(primaryLocation).save();
	}
}
