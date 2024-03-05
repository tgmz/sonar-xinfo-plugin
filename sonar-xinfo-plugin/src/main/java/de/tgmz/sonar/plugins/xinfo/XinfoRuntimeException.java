/*******************************************************************************
  * Copyright (c) 02.12.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo;

/**
 * Exception to indicate runtime errors.
 */
public class XinfoRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 8242050194743145608L;

	public XinfoRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
