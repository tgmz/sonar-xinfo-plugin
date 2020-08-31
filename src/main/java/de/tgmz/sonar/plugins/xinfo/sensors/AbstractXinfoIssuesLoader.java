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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import de.tgmz.sonar.plugins.xinfo.mc.McRegex;
import de.tgmz.sonar.plugins.xinfo.mc.McTemplate;
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
	private Map<String, Pattern> patternCache;

	public AbstractXinfoIssuesLoader(final FileSystem fileSystem, Language lang) {
		this.fileSystem = fileSystem;
		this.lang = lang;
		
		ruleMap = new TreeMap<>();
		patternCache = new TreeMap<>();
		
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
	
	protected void findMc(InputFile file, Charset charset) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.inputStream(), charset))) {
			String line;
			List<String> previousLines = new ArrayList<>();
			
			int i = 0;
			
			while ((line = br.readLine()) != null) {
				++i; 
				
				if (lang != Language.SAS && line.length() > 72) {
					line = line.substring(0,  72);
				}
				
				boolean ignoreable = isComment(line, lang, file.filename())
									|| isProbablyTest(previousLines, lang) 
									|| isProbablyInComment(previousLines, lang);
				
				for (McTemplate entry : PatternFactory.getInstance().getMcTemplates().getMcTemplate()) {
					if ("true".equals(entry.getIgnoreincomment()) && ignoreable) {
						LOGGER.debug("Ignoring {}", entry.getKey());
					} else {	
						for (McRegex r : entry.getMcRegex()) {
							Pattern p = patternCache.get(r.getvalue());
							
							if (p == null) {
								p = "false".equals(r.getCasesensitive()) ? Pattern.compile(r.getvalue(), Pattern.CASE_INSENSITIVE) :  Pattern.compile(r.getvalue());  
								
								patternCache.put(r.getvalue(), p);
							}
							
							List<String> split = Arrays.asList(r.getLang().split("\\,"));
							
							if (split.contains(lang.getKey()) || split.contains("all")) {
								MatcherResult mr = match(p, line);
							
								if (mr.getState() == MatcherResult.MatcherResultState.MATCH) {
									String desc = MessageFormat.format(ruleMap.get(entry.getKey()).getDescription(), mr.getMatch());
								
									saveIssue(file, i, entry.getKey(), desc, null);
								}
							}
						}
					}
				}
				
				previousLines.add(line.toUpperCase(Locale.ROOT).replaceAll("\\s+"," ")); //Replace all spaces with a single blank
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
	
	
	/**
	 * Matches a string against a pattern. Public for test purposes
	 * 
	 * @param p pattern
	 * @param s string
	 * @return result
	 */
	public static MatcherResult match(Pattern p, String s) {
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
		} catch (ExecutionException e) {
			LOGGER.error("Exectution error matching {} against {}", s, p, e);
			
			return new MatcherResult(MatcherResult.MatcherResultState.ERROR);
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted error matching {} against {}", s, p, e);
			
			Thread.currentThread().interrupt();
			
			return new MatcherResult(MatcherResult.MatcherResultState.ERROR);
		}
		
		return new MatcherResult(MatcherResult.MatcherResultState.MISMATCH);
	}
	
	private static boolean isComment(String line, Language lang, String fileName) {
		if (COMMENT.matcher(line).matches()) {
			return true;
		}
		
		if (lang == Language.SAS && line.trim().startsWith("*")) {
			return true;
		}
		
		if (lang == Language.ASSEMBLER && line.length() > 0 && "*".equals(line.substring(0, 1))) {
			return true;
		}
		
		if (lang == Language.COBOL && line.length() > 6 && "*".equals(line.substring(6, 7))) {
			return true;
		}
		
		if (lang == Language.MACRO && fileName.endsWith(".mac") && line.length() > 0 && "*".equals(line.substring(0, 1))) {
			return true;
		}
		
		if (lang == Language.MACRO && fileName.endsWith(".cpy") && line.length() > 6 && "*".equals(line.substring(6, 7))) {
			return true;
		}
		
		return false;
	}
	
	private static boolean isProbablyTest(List<String> lines, Language lang) {
		if (lang == Language.PLI) {
			if (!lines.isEmpty() && (lines.get(lines.size() - 1).contains(" IF LINKSTAT = 'X'")
									|| lines.get(lines.size() - 1).contains(" IF $CVT_LINKSTAT = 'X'"))) {
				return true;
			}
			
			if (lines.size() > 1) {
				for (int i = lines.size() - 1; i > 0; i--) {
					if (lines.get(i).contains(" END")) {
						return false;
					}
					
					if ((lines.get(i-1).contains(" IF LINKSTAT = 'X'") || lines.get(i-1).contains(" IF $CVT_LINKSTAT = 'X'"))
						&& lines.get(i).contains(" THEN DO;")) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	private static boolean isProbablyInComment(List<String> lines, Language lang) {
		if (lang == Language.PLI || lang == Language.SAS || lang == Language.MACRO) {
			if (!lines.isEmpty()) {
				for (int i = lines.size() - 1; i >= 0; i--) {
					if (lines.get(i).contains("*/")) {
						return false;
					}
					
					if (lines.get(i).contains("/*") && !lines.get(i).contains("*/")) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
}
