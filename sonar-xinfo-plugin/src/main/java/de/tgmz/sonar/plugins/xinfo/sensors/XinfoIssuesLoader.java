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
package de.tgmz.sonar.plugins.xinfo.sensors;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.rule.RuleKey;

import de.tgmz.sonar.plugins.xinfo.IXinfoProvider;
import de.tgmz.sonar.plugins.xinfo.RuleFactory;
import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.XinfoProviderFactory;
import de.tgmz.sonar.plugins.xinfo.config.XinfoProjectConfig;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.FILE;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.FILEREFERENCETABLE;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.MESSAGE;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.languages.XinfoLanguage;
import de.tgmz.sonar.plugins.xinfo.rules.XinfoRule;
import de.tgmz.sonar.plugins.xinfo.rules.XinfoRuleDefinition;


public class XinfoIssuesLoader implements Sensor {
	private static final Logger LOGGER = LoggerFactory.getLogger(XinfoIssuesLoader.class);
	
	private final Checks<XinfoRule> checks;
	private IXinfoProvider xinfoProvider;

	private static class Issue {
		InputFile inputFile;
		int line;
		String ruleKey;
		String message;
		@Override
		public String toString() {
			return "Issue [inputFile=" + inputFile + ", line=" + line + ", ruleKey=" + ruleKey + ", message=" + message + "]";
		}
	}
	
	private String[] includeLevels;
	
	public XinfoIssuesLoader(CheckFactory checkFactory) {
		checks = checkFactory.create(XinfoRuleDefinition.REPO_KEY);
		
		List<Class<?>> rules = RuleFactory.getInstance().getRules();
		
		checks.addAnnotatedChecks(rules.toArray(new Object[rules.size()]));
	}

	@Override
	public void describe(SensorDescriptor descriptor) {
		descriptor.name("XinfoIssuesLoader");
		descriptor.onlyOnLanguages(XinfoLanguage.KEY);
		descriptor.createIssuesForRuleRepository(XinfoRuleDefinition.REPO_KEY);
	}

	@Override
	public void execute(SensorContext context) {
		xinfoProvider = XinfoProviderFactory.getProvider(context.config());
		
		includeLevels = context.config().getStringArray(XinfoProjectConfig.XINFO_INCLUDE_LEVEL);
		
	    int threshold = context.config().getInt(XinfoProjectConfig.XINFO_LOG_THRESHOLD).orElse(100);

		FilePredicates p = context.fileSystem().predicates();
		
		int ctr = 0;
		
		for (InputFile inputFile : context.fileSystem().inputFiles(p.hasLanguages(XinfoLanguage.KEY))) {
			Language lang = Language.getByFilename(inputFile.filename());
			
			if (lang.canCompile()) {
				try {
					execute(context, inputFile, lang);
					
					if (++ctr % threshold == 0) {
						LOGGER.info("{} file(s) processed, current is {}", ctr, inputFile);
					}
				} catch (XinfoException e) {
					LOGGER.error("Cannot get xinfo for {}", inputFile, e);
				}
			}
		}
	}

	private void execute(SensorContext context, InputFile inputFile, Language lang) throws XinfoException {
		PACKAGE xinfo = xinfoProvider.getXinfo(inputFile);

		for (MESSAGE m : xinfo.getMESSAGE()) {
			Issue issue = computeIssue(m, xinfo.getFILEREFERENCETABLE(), inputFile, lang);
		
			if (issue != null) {
				RuleKey rk = RuleKey.of(XinfoRuleDefinition.REPO_KEY, issue.ruleKey);
			
				XinfoRule xr = getRule(rk);
			
				if (xr != null) {
					xr.execute(context, inputFile, rk, issue.message, issue.line);
				}
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
			result.message = message;
			result.line = Integer.parseInt(msgLine);
			result.inputFile = inputFile;
		} else {
			if (Arrays.asList(includeLevels).contains(String.valueOf(sev))) {
				try {
					FILE includeFile = XinfoUtil.computeFilefromFileNumber(frt, msgFile);
					String includedFromLine = XinfoUtil.computeIncludedFromLine(frt, includeFile, lang);
						
					result = new Issue();
							
					result.ruleKey = ruleKey;
					result.message = message;
					result.line = Integer.parseInt(includedFromLine);
					result.inputFile = inputFile;
							
					LOGGER.debug("Message {} refers to file {}. It was moved to file {}", m.getMSGNUMBER(), includeFile.getFILENAME(), inputFile);
				} catch (XinfoException e) {
					LOGGER.warn("Cannot get main file for message {} in file {}", m.getMSGNUMBER(), inputFile);
				}
			}
		}
		
		return result;
	}
	private XinfoRule getRule(RuleKey rk) {
		XinfoRule xr = checks.of(rk);
		
		if (xr == null) {
			Optional<Class<?>> first = RuleFactory.getInstance().getRules().stream().filter(c -> c.getSimpleName().endsWith(rk.rule())).findFirst();
			
			if (first.isPresent()) {
				try {
					xr = (XinfoRule) first.get().getDeclaredConstructor().newInstance();
				} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
					LOGGER.error("Cannot execute rule {} because checks.of() returned null and class cannot be instanciated", rk, e);
				}
			} else {
				LOGGER.error("Cannot execute rule {} because checks.of() returned null and class is not found", rk);
			}
		}
		
		return xr;
	}
}
