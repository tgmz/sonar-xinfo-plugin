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

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nullable;

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

import de.tgmz.sonar.plugins.xinfo.RuleFactory;
import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.XinfoProviderFactory;
import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;
import de.tgmz.sonar.plugins.xinfo.generated.Rule;
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
	private static final Logger LOGGER = Loggers.get(AbstractXinfoIssuesLoader.class);
	protected final FileSystem fileSystem;
	protected SensorContext context;
	private Language lang;
	private Map<String, Rule> ruleMap;
	public AbstractXinfoIssuesLoader(final FileSystem fileSystem, Language lang) {
		this.fileSystem = fileSystem;
		this.lang = lang;
		
		ruleMap = new TreeMap<>();
		
		for (Rule r: RuleFactory.getInstance().getRules(lang).getRule()) {
			ruleMap.put(r.getKey(), r);
		}
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
		
		Charset charset = Charset.forName(context.config().get(XinfoConfig.XINFO_ENCODING).orElse(Charset.defaultCharset().name()));
		
		Iterator<InputFile> fileIterator = fileSystem.inputFiles(fileSystem.predicates().hasLanguage(lang.getKey())).iterator();

		int ctr = 0;
		
		while (fileIterator.hasNext()) {
			InputFile inputFile = fileIterator.next();
			
			PACKAGE p;
			try {
				p = XinfoProviderFactory.getProvider(context.config()).getXinfo(inputFile);
			} catch (XinfoException e) {
				LOGGER.error("Error getting XINFO for file " + inputFile.filename(), e);
				
				continue;
			}
				
			createFindings(p, inputFile, charset);
			
			if (++ctr % 100 == 0) {
				LOGGER.info("{} files processed, current is {}", ctr, inputFile.filename());
			}
		}
	}

	private void createFindings(PACKAGE p, InputFile file, Charset charset) {
		for (MESSAGE m : p.getMESSAGE()) {
			try {
				Issue issue = computeIssue(p, m, file);
				
				if (issue != null) {
					Severity severity = null;
					
					String ruleKey = issue.ruleKey;
					
					if (lang == Language.COBOL) {
						// Chars at 3 and 4 indicate the compile phase which issued the message
						// In ErrMsg these chars are replaced by "XX"
						ruleKey = ruleKey.substring(0,  3) + "XX" + ruleKey.substring(5);
					}
					
					saveIssue(issue.inputFile, issue.line, ruleKey, issue.message, severity);
				}
			} catch (XinfoException e) {
				LOGGER.error("Error in xinfo on file {}", file, e);
			
				continue;
			}
		}
	}
	
	private Issue computeIssue(PACKAGE p, MESSAGE m, InputFile inputFile) throws XinfoException {
		String msgFile = m.getMSGFILE();
		
		if (StringUtils.isEmpty(msgFile)) {
			return null;
		}
		
		Issue result = new Issue();
		
		result.message = m.getMSGTEXT();
		result.ruleKey = m.getMSGNUMBER();
		
		if (XinfoUtil.isMainFile(msgFile, lang)) {
			result.line = Integer.parseInt(m.getMSGLINE());
			result.inputFile = inputFile;
		} else {
			result = null;
		}
		
		return result;
	}

	private void saveIssue(final InputFile inputFile, int line, String ruleKeyString, final String message, @Nullable Severity severity) {
		LOGGER.debug("Save issue {} for file {} on line {}", ruleKeyString, inputFile.filename(), line);
		
		RuleKey ruleKey = RuleKey.of(lang.getRepoKey(), ruleKeyString);

		NewIssue newIssue = context.newIssue().forRule(ruleKey);
		
		Rule r = ruleMap.get(ruleKeyString);
		
		if (r != null) {
			if (severity == null) {
				newIssue.overrideSeverity(Severity.valueOf(r.getSeverity()));
			} else {
				newIssue.overrideSeverity(severity);
			}
		} else {
			LOGGER.error("Xinfo message {} unknown", ruleKeyString);
			
			return;
		}
		
		NewIssueLocation primaryLocation = newIssue.newLocation().on(inputFile).message(message);
		
		int lineToSave = line;
		
		if (lineToSave > 0) {
			if (lineToSave > inputFile.lines()) {
				LOGGER.info("Linenumber {} for {} is outside range. It was reduced to {}", lineToSave, inputFile.filename(), inputFile.lines());
				
				lineToSave = inputFile.lines();
			}
			
			primaryLocation.at(inputFile.selectLine(lineToSave));
		}
		
		newIssue.at(primaryLocation).save();
	}
}
