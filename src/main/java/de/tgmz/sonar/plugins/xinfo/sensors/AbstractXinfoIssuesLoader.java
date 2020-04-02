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
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
import de.tgmz.sonar.plugins.xinfo.sensors.matcher.CallableMatcher;
import de.tgmz.sonar.plugins.xinfo.sensors.matcher.MatcherResult;

/**
 * This Sensor loads the results of an analysis performed by 
 * "real" mainframe compiler. Results are provided as an xml file
 * correspond to the rules defined in "&lt;language&gt;-rules.xml".
 */
public abstract class AbstractXinfoIssuesLoader implements Sensor {
	private static final Logger LOGGER = Loggers.get(AbstractXinfoIssuesLoader.class);
	private static final Pattern COMMENT = Pattern.compile("^\\s*\\/\\*.*(\\*\\/)?\\s*$");
	private static final int TIMEOUT = 1000;
	private static final ExecutorService executor = Executors.newFixedThreadPool( 10 );
	protected final FileSystem fileSystem;
	protected SensorContext context;
	private Language lang;
	private Map<String, Rule> ruleMap;
	private Map<String, List<Pattern>> mcPatternListMap;
	private McPattern mcPatterns;

	public AbstractXinfoIssuesLoader(final FileSystem fileSystem, Language lang) {
		this.fileSystem = fileSystem;
		this.lang = lang;
		
		ruleMap = new TreeMap<>();
		
		for (Rule r: RuleFactory.getInstance().getRules(lang).getRule()) {
			ruleMap.put(r.getKey(), r);
		}
		
		mcPatterns = PatternFactory.getInstance().getMcPatterns();
		
		mcPatternListMap = new TreeMap<>();
		
		for (Mc mc: mcPatterns.getMc()) {
			for (Regex r : mc.getRegex()) {
				String[] languagesForPattern = r.getLang().split("\\,");
				
				for (String languageForPattern : languagesForPattern) {
					if (lang.getKey().equals(languageForPattern) || "all".equals(r.getLang())) {
						Pattern p = "true".equals(r.getCasesensitive()) ? Pattern.compile(r.getvalue()) : Pattern.compile(r.getvalue(), Pattern.CASE_INSENSITIVE); 

						List<Pattern> list = mcPatternListMap.get(mc.getKey());
						
						if (list == null) { 
							list = new LinkedList<>();
							
							mcPatternListMap.put(mc.getKey(), list);
						}
						
						list.add(p);
					}
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
		
		Charset charset = Charset.forName(context.config().get(XinfoConfig.XINFO_ENCODING).orElse(System.getProperty("file.encoding")));
		
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
				
			createFindings(p, inputFile, charset);
			
			if (++ctr % 100 == 0) {
				LOGGER.info("{} files processed, current is {}", ctr, inputFile.filename());
			}
		}
	}

	private void createFindings(PACKAGE p, InputFile file, Charset charset) {
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
					
					saveIssue(file, effectiveMessageLine, ruleKey, message, severity);
				}
			} catch (XinfoException e) {
				LOGGER.error("Error in xinfo", e);
			
				continue;
			}
		}
		
		findMc(file, charset);
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
		
		Rule r = ruleMap.get(ruleKeyToSave);
		
		if (r != null) {
			if (severity == null) {
				newIssue.overrideSeverity(Severity.valueOf(r.getSeverity()));
			} else {
				newIssue.overrideSeverity(severity);
			}
		} else {
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
	
	protected void findMc(InputFile file, Charset charset) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.inputStream(), charset))) {
			String s;
			int i = 0;
			
			while ((s = br.readLine()) != null) {
				++i; 
				
				if (lang != Language.SAS && s.length() > 72) {
					s = s.substring(0,  72);
				}
				
				if (COMMENT.matcher(s).matches()) {
					continue; 	// Therefore we must increment i first!!!
				}
				
				for (Mc mc : mcPatterns.getMc()) {
					List<Pattern> pl = mcPatternListMap.get(mc.getKey());
					
					if (pl != null) {
						for (Pattern p : pl) {
							MatcherResult mr = match(p, s);
							
							if (mr.getState() == MatcherResult.MatcherResultState.MATCH) {
								String desc = MessageFormat.format(ruleMap.get(mc.getKey()).getDescription(), mr.getMatch());
								
								saveIssue(file, i, mc.getKey(), desc, null);
							}
						}
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
	private MatcherResult match(Pattern p, String s) {
		Future<String> fb = executor.submit(new CallableMatcher(p, s));
		
		try {
			String ms = fb.get(TIMEOUT, TimeUnit.MILLISECONDS);
			
			if (ms != null) {
				LOGGER.debug("String {} matched pattern [{}]", s, p);

				return new MatcherResult(MatcherResult.MatcherResultState.MATCH, ms.trim());
			}
		} catch (TimeoutException e) {
			LOGGER.error("Error matching {} against {} in less than {} millseconds, possible redos attack", s, p, TIMEOUT);
			
			return new MatcherResult(MatcherResult.MatcherResultState.ERROR);
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Error matching {} against {}", s, p, e);
			
			return new MatcherResult(MatcherResult.MatcherResultState.ERROR);
		}
		
		return new MatcherResult(MatcherResult.MatcherResultState.MISMATCH);
	}
}
