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

import java.io.IOException;
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
import de.tgmz.sonar.plugins.xinfo.XinfoFileAnalyzable;
import de.tgmz.sonar.plugins.xinfo.XinfoProviderFactory;
import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;
import de.tgmz.sonar.plugins.xinfo.generated.Rule;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.FILE;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.MESSAGE;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;
import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * This Sensor loads the results of an analysis performed by 
 * "real" mainframe compiler. Results are provided as an xml file
 * correspond to the rules defined in "&lt;language&gt;-rules.xml".
 */
public abstract class AbstractXinfoIssuesLoader implements Sensor {
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

	private void createFindings(PACKAGE p, InputFile file) {
		for (MESSAGE m : p.getMESSAGE()) {
			try {
				int effectiveMessageLine = computeEffectiveMessageLine(p, m);
				
				if (effectiveMessageLine > -1) {
					Severity severity = null;
					
					String ruleKey = m.getMSGNUMBER();
					String message = m.getMSGTEXT();
					
					if (context.config().getBoolean(XinfoConfig.XINFO_EXTRA).orElse(Boolean.FALSE)) {
						severity = computeExtraSeverity(file, ruleKey, message);
					}
					
					if (lang == Language.COBOL) {
						// Chars at 3 and 4 indicate the compile phase which issued the message
						// In ErrMsg these chars are replaced by "XX"
						ruleKey = ruleKey.substring(0,  3) + "XX" + ruleKey.substring(5);
					}
					
					saveIssue(file, effectiveMessageLine, ruleKey, message, severity);
				}
			} catch (XinfoException e) {
				LOGGER.error("Error in xinfo", e);
			
				continue;
			}
		}
	}

	private Severity computeExtraSeverity(InputFile file, String ruleKey, String message) {
		//The procedure procedure is not referenced.
		if ("IBM1213I W".equals(ruleKey) && message.contains("SEQERR")) {
			return Severity.INFO;
		}
		
		//Variable variable is unreferenced.
		if ("IBM2418I E".equals(ruleKey) && message.contains("PLERROR")) {
			return Severity.INFO;
		}
		
		//Argument to MAIN procedure is not CHARACTER VARYING.
		if ("IBM1195I W".equals(ruleKey)) {
			try {
				if (file.contents().contains("PIMS")) {
					return Severity.INFO;
				}
			} catch (IOException e) {
				LOGGER.warn("Cannot get contents of {}", file);
			}
		}
		
		return null;
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
