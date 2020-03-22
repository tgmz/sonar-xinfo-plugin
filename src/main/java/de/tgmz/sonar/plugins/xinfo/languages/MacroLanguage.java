/*******************************************************************************
  * Copyright (c) 21.03.2020 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.languages;

/**
 * This class defines the language of all kinds of macros e.g PL/I includes, COBOL copybooks or Assembler macros for Sonar.
 */
public final class MacroLanguage extends LanguageBridge {
	public MacroLanguage() {
		super(Language.MACRO.getKey(), Language.MACRO.getName());
	}
}
