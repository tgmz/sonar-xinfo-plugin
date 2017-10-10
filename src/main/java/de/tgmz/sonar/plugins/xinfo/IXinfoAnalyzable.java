/*******************************************************************************
  * Copyright (c) 03.12.2016 Thomas Zierer.
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

import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Interface for analyzable programs.
 */
public interface IXinfoAnalyzable {
	Language getLanguage();
	String getName();
	String getSource() throws IOException;
}
