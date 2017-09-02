/*******************************************************************************
  * Copyright (c) 13.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Factory for creating the sonar rules for a {@link Language}
 */
public final class RuleFactory {
	private static final Logger LOGGER = Loggers.get(RuleFactory.class);
	private static volatile RuleFactory instance;
	private DocumentBuilder db;
	private Unmarshaller unmarshaller;

	private RuleFactory() throws ParserConfigurationException, JAXBException {
		db = SecureDocumentBuilderFactory.getInstance().getDocumentBuilder();

		JAXBContext jaxbContext = JAXBContext.newInstance(XinfoRules.class);
		unmarshaller = jaxbContext.createUnmarshaller();
	}

	public static RuleFactory getInstance() {
		if (instance == null) {
			LOGGER.debug("Create new Factory instance");
			
			try {
				instance = new RuleFactory();
			} catch (ParserConfigurationException | JAXBException e) {
				String s = "Error creating rule factory";
				
				LOGGER.error(s, e);

				throw new XinfoRuntimeException(s, e);
			}
		}

		return instance;
	}

	@SuppressFBWarnings(value="XXE_DOCUMENT", justification="Not possible due to DocumentBuilderFactory settings")
	public XinfoRules getRules(Language l) {
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(l.getRulesDefinition())) {

			Document doc = db.parse(new InputSource(is));

			return (XinfoRules) unmarshaller.unmarshal(doc);
		} catch (IOException | SAXException | JAXBException e) {
			String s = "Error parsing rules";
			
			LOGGER.error(s, e);

			throw new XinfoRuntimeException(s, e);
		}
	}
}
