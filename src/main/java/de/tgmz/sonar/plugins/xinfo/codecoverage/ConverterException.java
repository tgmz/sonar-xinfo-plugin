/*******************************************************************************
  * Copyright (c) 08.08.2017 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.codecoverage;

public class ConverterException extends Exception {
	private static final long serialVersionUID = -301248969909100947L;

	public ConverterException(String message, Throwable cause) {
		super(message, cause);
	}
}
