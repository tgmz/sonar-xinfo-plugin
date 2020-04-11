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
package de.tgmz.sonar.plugins.xinfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.mc.Mc;
import de.tgmz.sonar.plugins.xinfo.mc.McPattern;
import de.tgmz.sonar.plugins.xinfo.mc.Regex;

/**
 * Factory for creating the sonar rules for a {@link Language}
 */
public final class PatternFactory {
	private static final Logger LOGGER = Loggers.get(PatternFactory.class);
	private static volatile PatternFactory instance;
	private Map<Language, Map<String, List<Pattern>>> mcPatternListMap = new TreeMap<>();

	private PatternFactory() throws ParserConfigurationException, JAXBException {
		DocumentBuilder db = SecureDocumentBuilderFactory.getInstance().getDocumentBuilder();

		JAXBContext jaxbContext = JAXBContext.newInstance(McPattern.class);
		Unmarshaller mcum = jaxbContext.createUnmarshaller();
		
		McPattern mcPatterns; 
		
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("mc-pattern.xml")) {

			Document doc = db.parse(new InputSource(is));

			mcPatterns = (McPattern) mcum.unmarshal(doc);
		} catch (IOException | SAXException | JAXBException e) {
			String s = "Error parsing rules";
			
			throw new XinfoRuntimeException(s, e);
		}
		
		for(Language l : Language.values()) {
			mcPatternListMap.put(l, compileMcPatterns(l, mcPatterns));
		}
	}

	public static PatternFactory getInstance() {
		if (instance == null) {
			LOGGER.debug("Create new Factory instance");
			
			try {
				instance = new PatternFactory();
			} catch (ParserConfigurationException | JAXBException e) {
				String s = "Error creating rule factory";
				
				throw new XinfoRuntimeException(s, e);
			}
		}

		return instance;
	}

	public Map<String, List<Pattern>> getMcPatterns(Language lang) {
		return mcPatternListMap.get(lang);
	}
	
	private Map<String, List<Pattern>> compileMcPatterns(Language lang, McPattern mcPatterns) {
		Map<String, List<Pattern>> result = new TreeMap<>();
		
		for (Mc mc: mcPatterns.getMc()) {
			for (Regex r : mc.getRegex()) {
				String[] languagesForPattern = r.getLang().split("\\,");
				
				for (String languageForPattern : languagesForPattern) {
					if (lang.getKey().equals(languageForPattern) || "all".equals(r.getLang())) {
						Pattern p = "true".equals(r.getCasesensitive()) ? Pattern.compile(r.getvalue()) : Pattern.compile(r.getvalue(), Pattern.CASE_INSENSITIVE); 

						List<Pattern> list = result.get(mc.getKey());
						
						if (list == null) { 
							list = new LinkedList<>();
							
							result.put(mc.getKey(), list);
						}
						
						list.add(p);
					}
				}
			}
		}

		return result;
	}
}
