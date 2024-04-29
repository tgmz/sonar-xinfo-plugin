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

public class OtfException extends XinfoException {
	private static final long serialVersionUID = 1147481524499921473L;

	public OtfException(String arg0) {
		super(arg0);
	}

	public OtfException(String arg0, Exception arg1) {
		super (arg0, arg1);
	}
}
