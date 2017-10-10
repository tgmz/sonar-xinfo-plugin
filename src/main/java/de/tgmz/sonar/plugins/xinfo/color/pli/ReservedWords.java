/*******************************************************************************
  * Copyright (c) 13.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.color.pli;

/**
 * PL/I "keywords". I am perfectly aware that PL/I does not know reservered words but this is the most effective
 * way to colorize a PL/I program.
 */
public enum ReservedWords {
	XMLOMIT,
	XMLATTR,
	DCL,
	DECLARE,
	DEFINE,
	STRUCTURE,
	INIT,
	VALUE,
	BIN,
	BINARY,
	DEC,
	DECIMAL,
	FIXED,
	FLOAT,
	CHAR,
	CHARACTER,
	PIC,
	PICTURE,
	BIT,
	PTR,
	POINTER,
	ALIGNED,
	UNAL,
	UNALIGNED,
	NONASGN,
	NONASSIGNABLE,
	STATIC,
	AUTO,
	AUTOMATIC,
	BASED,
	LIKE,
	DEF,
	DEFINED,
	BUILTIN,
	PROC,
	PROCEDURE,
	LIMITED,
	ENTRY,
	EXT,
	EXTERNAL,
	OPTIONS,
	FETCH,
	CALL,
	GOTO,
	PUT,
	SKIP,
	LIST,
	EDIT,
	BEGIN,
	END,
	RETURN,
	RETURNS,
	SELECT,
	WHEN,
	OTHER,
	OTHERWISE,
	DO,
	WHILE,
	IF,
	THEN,
	ELSE,
	FILE,
	RECORD,
	SEQL,
	STREAM,
	INPUT,
	OUTPUT,
	PRINT,
	;
}
