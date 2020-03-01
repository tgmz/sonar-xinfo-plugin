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
import de.tgmz.sonar.plugins.xinfo.mc.McPattern;

/**
 * Factory for creating the sonar rules for a {@link Language}
 */
public final class PatternFactory {
	private static final Logger LOGGER = Loggers.get(PatternFactory.class);
	private static volatile PatternFactory instance;
	private DocumentBuilder db;
	private Unmarshaller mcum;

	private PatternFactory() throws ParserConfigurationException, JAXBException {
		db = SecureDocumentBuilderFactory.getInstance().getDocumentBuilder();

		JAXBContext jaxbContext = JAXBContext.newInstance(McPattern.class);
		mcum = jaxbContext.createUnmarshaller();
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

	public McPattern getMcPatterns() {
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("mc-pattern.xml")) {

			Document doc = db.parse(new InputSource(is));

			return (McPattern) mcum.unmarshal(doc);
		} catch (IOException | SAXException | JAXBException e) {
			String s = "Error parsing rules";
			
			throw new XinfoRuntimeException(s, e);
		}
	}
}
