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
import de.tgmz.sonar.plugins.xinfo.SonarRule;
import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.XinfoFileAnalyzable;
import de.tgmz.sonar.plugins.xinfo.XinfoProviderFactory;
import de.tgmz.sonar.plugins.xinfo.XinfoRules;
import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.plicomp.FILE;
import de.tgmz.sonar.plugins.xinfo.plicomp.MESSAGE;
import de.tgmz.sonar.plugins.xinfo.plicomp.PACKAGE;

/**
 * This Sensor loads the results of an analysis performed by 
 * "real" mainframe compiler. Results are provided as an xml file
 * correspond to the rules defined in "&lt;language&gt;-rules.xml".
 */
public abstract class AbstractXinfoIssuesLoader implements Sensor {
	private static final Logger LOGGER = Loggers.get(AbstractXinfoIssuesLoader.class);
	private final FileSystem fileSystem;
	private SensorContext context;
	private XinfoRules xinfoRules;
	private Language lang;

	public AbstractXinfoIssuesLoader(final FileSystem fileSystem, Language lang) {
		this.fileSystem = fileSystem;
		this.lang = lang;
		
		xinfoRules = RuleFactory.getInstance().getRules(lang);
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
				p = XinfoProviderFactory.getProvider(context.config()).getXinfo(new XinfoFileAnalyzable(lang, inputFile));
			} catch (XinfoException e) {
				LOGGER.error("Error getting XINFO for file " + inputFile.filename(), e);
				
				continue;
			}
				
			createFindings(p, inputFile);
			
			if (++ctr % 100 == 0) {
				LOGGER.info("{} files processed, current is {}", ctr, inputFile.filename());
			}
		}
	}

	private void saveIssue(final InputFile inputFile, int line, String externalRuleKey, final String message, @Nullable Severity severity) {
		LOGGER.debug("Save issue {} for file {} on line {}", externalRuleKey, inputFile.filename(), line);
		
		String ruleKeyToSave;
		
		if (lang == Language.COBOL) {
			// Chars at 3 and 4 indicate the compile phase which issued the message
			// In ErrMsg these chars are replaced by "XX"
			ruleKeyToSave = externalRuleKey.substring(0,  3) + "XX" + externalRuleKey.substring(5);
		} else {
			ruleKeyToSave = externalRuleKey;
		}
		
		RuleKey ruleKey = RuleKey.of(lang.getRepoKey(), ruleKeyToSave);

		NewIssue newIssue = context.newIssue().forRule(ruleKey);
		
		boolean found = false;
		
		for (Iterator<SonarRule> iterator = xinfoRules.getRules().iterator(); iterator.hasNext() && !found;) {
			SonarRule r = iterator.next();
			
			if (ruleKeyToSave.equals(r.getKey())) {
				if (severity == null) {
					newIssue.overrideSeverity(Severity.valueOf(r.getSeverity()));
				} else {
					newIssue.overrideSeverity(severity);
				}
				
				found = true;
			}
		}

		if (!found) {
			LOGGER.error("Xinfo message {} unknown", ruleKeyToSave);
			
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
	
	private void createFindings(PACKAGE p, InputFile file) {
		for (MESSAGE m : p.getMESSAGE()) {
			try {
				int effectiveMessageLine = computeEffectiveMessageLine(p, m);
				
				if (effectiveMessageLine > -1) {
					Severity severity = null;
					
					String ruleKey = m.getMSGNUMBER();
					String message = m.getMSGTEXT();
					
					if (context.config().getBoolean(XinfoConfig.XINFO_BLB).orElse(Boolean.FALSE)) {
						//The procedure procedure is not referenced.
						if ("IBM1213I W".equals(ruleKey) && message.contains("SEQERR")) {
							severity = Severity.INFO;
						}
						
						//Variable variable is unreferenced.
						if ("IBM2418I E".equals(ruleKey) && message.contains("PLERROR")) {
							severity = Severity.INFO;
						}
						
						//variable name is declared as BASED on the ADDR of variable name,
						//but variable name requires more storage than variable name.
						if ("IBM2402I E".equals(ruleKey)) {
							severity = Severity.BLOCKER;
						}
						
						//RETURN statement without an expression is invalid inside a subprocedure that specified the RETURNS attribute.
						if ("IBM2409I E".equals(ruleKey)) {
							severity = Severity.BLOCKER;
						}
						
						//Scale factor is less than 0.  
						if ("IBM2452I E".equals(ruleKey)) {
							severity = Severity.BLOCKER;
						}
						
						//SELECT statement contains no OTHERWISE clause
						if ("IBM1059I I".equals(ruleKey)) {
							severity = Severity.MAJOR;
						}
						
						//Arithmetic operands should both be numeric
						if ("IBM1247I E".equals(ruleKey)) {
							severity = Severity.BLOCKER;
						}
						
						//The variable variable name is declared without any data attributes
						if ("IBM1482I E".equals(ruleKey)) {
							severity = Severity.BLOCKER;
						}
						
						//Variable variable name is implicitly declared
						if ("IBM1373I E".equals(ruleKey)) {
							severity = Severity.BLOCKER;
						}
						
						//RULES(NOLAXIF) requires BIT(1) expressions in IF, WHILE, etc
						if ("IBM1274I E".equals(ruleKey)) {
							severity = Severity.BLOCKER;
						}
						
						//Scale factor is larger than the precision
						if ("IBM2436I E".equals(ruleKey)) {
							severity = Severity.BLOCKER;
						}
					}
					
					saveIssue(file, effectiveMessageLine, ruleKey, message, severity);
				}
			} catch (XinfoException e) {
				LOGGER.error("Error in xinfo", e);
			
				continue;
			}
		}
	}
	private int computeEffectiveMessageLine(PACKAGE p, MESSAGE m) throws XinfoException {
		String msgFile = m.getMSGFILE();
		
		if (StringUtils.isEmpty(msgFile)) {
			return -1;
		}
		
		if (XinfoUtil.isMainFile(msgFile, lang)) {
			return Integer.parseInt(m.getMSGLINE());
		} else {
			if (context.config().getBoolean(XinfoConfig.IGNORE_INCLUDES).orElse(Boolean.FALSE)) {
				return -1;
			} else {
				FILE f = XinfoUtil.computeFilefromFileNumber(p.getFILEREFERENCETABLE(), msgFile);
				
				if (f.getINCLUDEDFROMFILE() == null || f.getINCLUDEDONLINE() == null) {
					return -1;
				}
	
				// Get the line number where it was included.
				return Integer.parseInt(XinfoUtil.computeIncludedFromLine(p.getFILEREFERENCETABLE(), f, lang));
			}
		}
	}
}
