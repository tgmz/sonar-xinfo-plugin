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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.FILE;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.FILEREFERENCETABLE;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.MESSAGE;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;
import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * This Sensor loads the results of an analysis performed by 
 * "real" mainframe compiler. Results are provided as an xml or events file
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
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractXinfoIssuesLoader.class);
	protected final FileSystem fileSystem;
	protected SensorContext context;
	private List<Language> languages;
	
	protected AbstractXinfoIssuesLoader(final FileSystem fileSystem, List<Language> languages) {
		this.fileSystem = fileSystem;
		this.languages = languages;
	}

	@Override
	public void describe(final SensorDescriptor descriptor) {
		StringBuilder s = new StringBuilder();
		List<String> onlyOnLanguages = new LinkedList<>();
		
		languages.forEach(n -> {
			s.append(n.getName() + " "); 
			onlyOnLanguages.add(n.getKey());
		});
		
		descriptor.name(s.toString() + "Issues Loader Sensor");
		descriptor.onlyOnLanguages(onlyOnLanguages.toArray(new String[languages.size()]));
		
	}
	
	@Override
	public void execute(final SensorContext aContext) {
		LOGGER.debug("execute");

		this.context = aContext;
		
	    int threshold = context.config().getInt(XinfoConfig.XINFO_LOG_THRESHOLD).orElse(100);

		Collection<String> c = new LinkedList<>();
		
		languages.forEach(n -> c.add(n.getKey()));
	    
		Iterator<InputFile> fileIterator = fileSystem.inputFiles(fileSystem.predicates().hasLanguages(c)).iterator();

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
				
			createFindings(p, inputFile, Language.getByKey(inputFile.language()));
			
			if (++ctr % threshold == 0) {
				LOGGER.info("{} files processed, current is {}", ctr, inputFile);
			}
		}
	}

	private void createFindings(PACKAGE p, InputFile inputFile, Language lang) {
		for (MESSAGE m : p.getMESSAGE()) {
			Issue issue = computeIssue(m, p.getFILEREFERENCETABLE(), inputFile, lang);
				
			if (issue != null) {
				saveIssue(issue, lang);
			}
		}
	}
	
	private Issue computeIssue(MESSAGE m, FILEREFERENCETABLE frt, InputFile inputFile, Language lang) {
		String msgFile = m.getMSGFILE();
		String msgLine = m.getMSGLINE();
		
		// For MACRO or preprocessor messages, MSGFILE and MSGLINE may be null
		if (StringUtils.isEmpty(msgFile)) {
			msgFile = XinfoUtil.getMainFileNumber(lang);
		}
		
		if (StringUtils.isEmpty(msgLine)) {
			msgLine = "1";
		}
		
		String message = m.getMSGTEXT();
		String ruleKey = m.getMSGNUMBER();
		
		int idx = ruleKey.length();
		char sev = ruleKey.charAt(idx - 1);
		
		Severity severity = computeSeverity(sev);
		
		ruleKey = lang == Language.COBOL ? 
					// Sample COBOL: "IGYPS2145-E"
					// Chars at 3 and 4 indicate the compile phase which issued the message
					// In ErrMsg these chars are replaced by "XX"
				ruleKey.substring(0, 3) + "XX" + ruleKey.substring(5, idx - 2) :
					// Sample PL/I: "IBM1316I E"
					// Sample HLA: "ASMA057E"
					// Sample C/C++: "CCN3078W"
				ruleKey.substring(0, idx - 1).trim();

		Issue result = null;
		
		if (msgFile.equals(XinfoUtil.getMainFileNumber(lang))) {
			result = new Issue();
			
			result.ruleKey = ruleKey;
			result.severity = severity;
			result.message = message;
			result.line = Integer.parseInt(msgLine);
			result.inputFile = inputFile;
		} else {
			Optional<String> optional = context.config().get(XinfoConfig.XINFO_INCLUDE_LEVEL);
			
			if (optional.isPresent()) {
				String[] levels = optional.get().split(",");
				
				if (Arrays.asList(levels).contains(String.valueOf(sev))) {
					try {
						FILE includeFile = XinfoUtil.computeFilefromFileNumber(frt, msgFile);
						String includedFromLine = XinfoUtil.computeIncludedFromLine(frt, includeFile, lang);
						
						result = new Issue();
							
						result.ruleKey = ruleKey;
						result.severity = severity;
						result.message = message;
						result.line = Integer.parseInt(includedFromLine);
						result.inputFile = inputFile;
							
						LOGGER.debug("Message {} refers to file {}. It was moved to file {}", m.getMSGNUMBER(), includeFile.getFILENAME(), inputFile);
					} catch (XinfoException e) {
						LOGGER.warn("Cannot get main file for message {} in file {}", m.getMSGNUMBER(), inputFile);
					}
				}
			}
		}
		
		return result;
	}

	private void saveIssue(final Issue issue, Language lang) {
		LOGGER.debug("Save issue {}", issue);
		
		RuleKey ruleKey = RuleKey.of(lang.getRepoKey(), issue.ruleKey);

		NewIssue newIssue = context.newIssue().forRule(ruleKey);
		
		if (issue.severity != null) {
			newIssue.overrideImpact(SoftwareQuality.RELIABILITY, issue.severity);
		}
		
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
			return Severity.LOW;
		case 'W':
			return Severity.MEDIUM;
		case 'E':
		case 'S':
		case 'U':
		default:
			return Severity.HIGH;
		}

	}
}
