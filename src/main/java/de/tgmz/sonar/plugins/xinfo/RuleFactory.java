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

import javax.xml.parsers.DocumentBuilder;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.tgmz.sonar.plugins.xinfo.generated.XinfoRules;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Factory for creating the sonar rules for a {@link Language}
 */
public final class RuleFactory {
	private static final Logger LOGGER = Loggers.get(RuleFactory.class);
	private static RuleFactory instance;
	private DocumentBuilder db;
	private Unmarshaller xium;

	private RuleFactory() throws JAXBException {
		db = SecureDocumentBuilderFactory.getInstance().getDocumentBuilder();

		JAXBContext jaxbContext = JAXBContext.newInstance(XinfoRules.class);
		xium = jaxbContext.createUnmarshaller();
	}

	public static synchronized RuleFactory getInstance() {
		if (instance == null) {
			LOGGER.debug("Create new Factory instance");
			
			try {
				instance = new RuleFactory();
			} catch (JAXBException e) {
				String s = "Error creating rule factory";
				
				throw new XinfoRuntimeException(s, e);
			}
		}

		return instance;
	}

	@SuppressFBWarnings(value="XXE_DOCUMENT", justification="Not possible due to DocumentBuilderFactory settings")
	public XinfoRules getRules(Language l) {
		XinfoRules result;
		
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(l.getRulesDefinition())) {
			Document doc = db.parse(new InputSource(is));

			result = (XinfoRules) xium.unmarshal(doc);
		} catch (IOException | SAXException | JAXBException e) {
			String s = "Error parsing rules";
			
			throw new XinfoRuntimeException(s, e);
		}
		
		return result;
	}
}
