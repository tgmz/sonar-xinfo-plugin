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

import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.languages.Language;

public interface IConnectable {
	void submit(String jcl) throws XinfoException;
	byte[] retrieve(String dsn) throws XinfoException;
	void write(String dsn, String content) throws XinfoException;
	void deleteDsn(String dsn) throws XinfoException;
	String createAndUploadInputDataset(Language lang, String content) throws XinfoException;
	String createSysxml() throws XinfoException;
}
