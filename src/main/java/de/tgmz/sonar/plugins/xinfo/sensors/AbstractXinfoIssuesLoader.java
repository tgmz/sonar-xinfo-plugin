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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

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

import de.tgmz.sonar.plugins.xinfo.PatternFactory;
import de.tgmz.sonar.plugins.xinfo.Rule;
import de.tgmz.sonar.plugins.xinfo.RuleFactory;
import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.XinfoFileAnalyzable;
import de.tgmz.sonar.plugins.xinfo.XinfoProviderFactory;
import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.mc.Mc;
import de.tgmz.sonar.plugins.xinfo.mc.McPattern;
import de.tgmz.sonar.plugins.xinfo.mc.Regex;
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
	private static final Pattern COMMENT = Pattern.compile("^\\s*\\/\\*.*(\\*\\/)?\\s*$");
	private final FileSystem fileSystem;
	private SensorContext context;
	private Language lang;
	private Map<String, Rule> ruleMap;
	private Map<String, Pattern> mcPatternMap;
	private McPattern mcPatterns;

	public AbstractXinfoIssuesLoader(final FileSystem fileSystem, Language lang) {
		this.fileSystem = fileSystem;
		this.lang = lang;
		
		ruleMap = new TreeMap<>();
		
		for (Rule r: RuleFactory.getInstance().getRules(lang).getRule()) {
			ruleMap.put(r.getKey(), r);
		}
		
		mcPatterns = PatternFactory.getInstance().getMcPatterns();
		
		mcPatternMap = new TreeMap<>();
		
		for (Mc mc: mcPatterns.getMc()) {
			for (Regex r : mc.getRegex()) {
				if (lang.getKey().equals(r.getLang()) || "all".equals(r.getLang())) {
					Pattern p = "true".equals(r.getCasesensitive()) ? Pattern.compile(r.getvalue()) : Pattern.compile(r.getvalue(), Pattern.CASE_INSENSITIVE); 
					
					mcPatternMap.put(mc.getKey(), p);
				}
			}
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
		
		Rule r = ruleMap.get(ruleKeyToSave);
		
		if (r != null) {
			if (severity == null) {
				newIssue.overrideSeverity(Severity.valueOf(r.getSeverity()));
			} else {
				newIssue.overrideSeverity(severity);
			}
		} else {
			if (!found) {
				LOGGER.error("Xinfo message {} unknown", ruleKeyToSave);
			
				return;
			}
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
						
						//Argument to MAIN procedure is not CHARACTER VARYING.
						if ("IBM1195I W".equals(ruleKey)) {
							try {
								if (file.contents().contains("PIMS")) {
									severity = Severity.INFO;
								}
							} catch (IOException e) {
								LOGGER.warn("Cannot get contents of {}", file);
							}
						}
					}
					
					saveIssue(file, effectiveMessageLine, ruleKey, message, severity);
				}
			} catch (XinfoException e) {
				LOGGER.error("Error in xinfo", e);
			
				continue;
			}
		}
		
		findMc(file);
	}
	private void findMc(InputFile file) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.inputStream()))) {
			String s;
			int i = 0;
			
			while ((s = br.readLine()) != null) {
				++i; 
				
				if (s.length() > 72) {
					s = s.substring(0,  72);
				}
				
				if (COMMENT.matcher(s).matches()) {
					continue; 	// Therefore we must increment i first!!!
				}
				
				for (Mc mc : mcPatterns.getMc()) {
					Pattern p = mcPatternMap.get(mc.getKey());
					
					if (p != null && p.matcher(s).matches()) {
						saveIssue(file, i, mc.getKey(), ruleMap.get(mc.getKey()).getDescription(), null);
					}
				}	
			}
		} catch (IOException e) {
			LOGGER.error("Error reading {}", file, e);
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
