/*******************************************************************************
  * Copyright (c) 20.01.2017 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * Factory for secure DocumentBuilders.
 */
public final class SecureDocumentBuilderFactory {
	private static final Logger LOGGER = Loggers.get(SecureDocumentBuilderFactory.class);
	private DocumentBuilder documentBuilder;
	private static SecureDocumentBuilderFactory instance;

	private SecureDocumentBuilderFactory() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		LOGGER.debug("Using DocumentBuilderFactory {}", dbf.getClass().getCanonicalName());
		
		dbf.setValidating(false);
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true); // cf. findsecbugs XXE_DOCUMENT
		dbf.setFeature("http://apache.org/xml/features/validation/schema", false);
		dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
		dbf.setFeature("http://xml.org/sax/features/validation", false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		dbf.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
		
		documentBuilder = dbf.newDocumentBuilder();
	}

	public static synchronized SecureDocumentBuilderFactory getInstance() {
		if (instance == null) {
			LOGGER.debug("Create new SecureDocumentBuilderFactory instance");
			
			try {
				instance = new SecureDocumentBuilderFactory();
			} catch (ParserConfigurationException e) {
				throw new XinfoRuntimeException("Error creating rule factory", e);
			}
		}

		return instance;
	}
	
	public DocumentBuilder getDocumentBuilder() {
		return documentBuilder;
	}
}
