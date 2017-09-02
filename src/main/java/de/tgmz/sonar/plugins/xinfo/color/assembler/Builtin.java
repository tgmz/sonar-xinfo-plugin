/*******************************************************************************
  * Copyright (c) 13.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution,  and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.color.assembler;

/**
 * Assembler builtin functions.
 */
public enum Builtin {
	AND, B2A, C2A, D2A, DCLEN, FIND, INDEX, NOT, OR, SLA, SLL, SRA, SRL, X2A, XOR,
	ISBIN, ISDEC, ISHEX, ISSYM,
	A2B, A2C, A2D, A2X, B2C, B2D, B2X, BYTE, C2B, C2D, C2X, D2B, D2C, D2X, DCVAL,
	DEQUOTE, DOUBLE, LOWER, SIGNED, UPPER, X2B, X2C, X2D,
}