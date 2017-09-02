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

/**
 * Exception to indicate exceptions parsing a xinfo.xml.
 */
public class XinfoException extends Exception {
	private static final long serialVersionUID = 8242050194743145608L;

	public XinfoException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public XinfoException(String arg0) {
		super(arg0);
	}
}
