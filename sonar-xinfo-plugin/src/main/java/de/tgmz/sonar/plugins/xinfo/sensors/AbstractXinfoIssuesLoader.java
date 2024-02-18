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

import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.XinfoProviderFactory;
import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;
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
		Severity severity;
		@Override
		public String toString() {
			return "Issue [inputFile=" + inputFile + ", line=" + line + ", ruleKey=" + ruleKey + ", message=" + message
					+ ", severity=" + severity + "]";
		}
	}
	private static final Logger LOGGER = Loggers.get(AbstractXinfoIssuesLoader.class);
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
		
	    int threshold = context.config().getInt(XinfoConfig.XINFO_LOG_THRESHOLD).orElse(100);
	    
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
			
			if (++ctr % threshold == 0) {
				LOGGER.info("{} files processed, current is {}", ctr, inputFile);
			}
		}
	}

	private void createFindings(PACKAGE p, InputFile file) {
		for (MESSAGE m : p.getMESSAGE()) {
			Issue issue = computeIssue(m, file);
				
			if (issue != null) {
				saveIssue(issue);
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
			// Sample COBOL: "IGYPS2145-E"
			// Chars at 3 and 4 indicate the compile phase which issued the message
			// In ErrMsg these chars are replaced by "XX"
			int idx = result.ruleKey.length();
			
			result.severity = computeSeverity(result.ruleKey.charAt(idx - 1));
			result.ruleKey = result.ruleKey.substring(0, 3) + "XX" + result.ruleKey.substring(5, idx - 2);
		} else {
			// Sample PL/I: "IBM1316I E"
			// Sample HLA: "ASMA057E"
			// Sample C/C++: "CCN3078W"
			int idx = result.ruleKey.length();
			
			result.severity = computeSeverity(result.ruleKey.charAt(idx - 1));
			result.ruleKey = result.ruleKey.substring(0, idx - 1).trim();
		}
			
		if (XinfoUtil.isMainFile(msgFile, lang)) {
			result.line = Integer.parseInt(m.getMSGLINE());
			result.inputFile = inputFile;
		} else {
			result = null;
		}
		
		return result;
	}

	private void saveIssue(final Issue issue) {
		LOGGER.debug("Save issue {}", issue);
		
		RuleKey ruleKey = RuleKey.of(lang.getRepoKey(), issue.ruleKey);

		NewIssue newIssue = context.newIssue().forRule(ruleKey);
		
		NewIssueLocation primaryLocation = newIssue.newLocation().on(issue.inputFile).message(issue.message);
		
		int lineToSave = issue.line;
		
		if (lineToSave > 0) {
			int maxLine = issue.inputFile.lines();
			if (lineToSave > maxLine) {
				LOGGER.info("Linenumber for issue {} is outside range. It was reduced to {}", issue, maxLine);
				
				lineToSave = issue.inputFile.lines();
			}
			
			primaryLocation.at(issue.inputFile.selectLine(lineToSave));
		}
		
		newIssue.at(primaryLocation).save();
	}
	private static Severity computeSeverity(char c) {
		switch (c) {
		case 'I':
			return Severity.INFO;
		case 'W':
			return Severity.MINOR;
		case 'E':
			return Severity.MAJOR;
		case 'S':
			return Severity.CRITICAL;
		case 'U':
		default:
			return Severity.BLOCKER;
		}

	}
}
