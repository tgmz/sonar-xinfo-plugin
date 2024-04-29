/*******************************************************************************
  * Copyright (c) 29.04.2024 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.otf;

import de.tgmz.sonar.plugins.xinfo.languages.Language;

public interface IConnectable {
	void submit(String jcl) throws OtfException;
	byte[] retrieve(String dsn) throws OtfException;
	void write(String dsn, String content) throws OtfException;
	void deleteDsn(String dsn) throws OtfException;
	String createAndUploadInputDataset(Language lang, String content) throws OtfException;
	String createSysxml() throws OtfException;
}
