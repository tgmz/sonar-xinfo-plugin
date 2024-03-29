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
package de.tgmz.sonar.plugins.xinfo.sensors;

import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.FILE;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.FILEREFERENCETABLE;
import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Some utility methods to walk through a {@link de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE}.
 */
public final class XinfoUtil {
	private XinfoUtil() {
		// private constructor to hide the implicit public one
	}
	
	public static FILE computeFilefromFileNumber(FILEREFERENCETABLE frt, String idx) throws XinfoException {
		for (FILE f : frt.getFILE()) {
			if (idx.equals(f.getFILENUMBER())) {
				return f;
			}
		}

		throw new XinfoException("Cannot compute file for filenumber " + idx);
	}

	public static String computeIncludedFromLine(FILEREFERENCETABLE filereferencetable, FILE f, Language lang) throws XinfoException {
		if (f.getINCLUDEDFROMFILE() == null || f.getINCLUDEDONLINE() == null) {
			throw new XinfoException("Cannot compute line number");
		}
		
		if ("1".equals(f.getINCLUDEDFROMFILE())) {
			// The file was included from the main file itself.
			// Return the line where it was included
			return f.getINCLUDEDONLINE();
		} else {
			// Get the file which INCLUDEded the original file ...
			// Attention: In PL/I INCLUDEDFROMFILE == FILEREFERNCETABLE + 1 !!!!
			int i = Integer.parseInt(f.getINCLUDEDFROMFILE()) - (lang == Language.PLI ? 1 : 0);
			FILE ff = computeFilefromFileNumber(filereferencetable, String.valueOf(i));

			// ... and compute the line where it was included
			return computeIncludedFromLine(filereferencetable, ff, lang);
		}
	}
	
	public static String getMainFileNumber(Language lang) {
		return lang == Language.PLI ? "0" : "1";	// Crazy but that that's how it is
	}
}
