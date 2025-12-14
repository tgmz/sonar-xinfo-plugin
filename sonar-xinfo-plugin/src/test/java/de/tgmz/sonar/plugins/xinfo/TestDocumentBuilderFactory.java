/*******************************************************************************
  * Copyright (c) 10.09.2017 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Dummy DocumentBuilderFactory to force {@link ParserConfigurationException}.
 */
public class TestDocumentBuilderFactory extends DocumentBuilderFactory {
	
	@Override
	public Object getAttribute(String name) throws IllegalArgumentException {
		return null;
	}

	@Override
	public boolean getFeature(String name) throws ParserConfigurationException {
		return false;
	}

	@Override
	public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
		throw new ParserConfigurationException();
	}

	@Override
	public void setAttribute(String name, Object value) throws IllegalArgumentException {
		// Do nothing
	}

	@Override
	public void setFeature(String name, boolean value) throws ParserConfigurationException {
		throw new ParserConfigurationException();
	}
}