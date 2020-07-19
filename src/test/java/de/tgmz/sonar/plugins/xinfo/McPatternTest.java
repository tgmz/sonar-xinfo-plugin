/*******************************************************************************
  * Copyright (c) 09.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/

package de.tgmz.sonar.plugins.xinfo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

import de.tgmz.sonar.plugins.xinfo.mc.McRegex;
import de.tgmz.sonar.plugins.xinfo.mc.McTemplate;
import de.tgmz.sonar.plugins.xinfo.sensors.AbstractXinfoIssuesLoader;
import de.tgmz.sonar.plugins.xinfo.sensors.matcher.MatcherResult.MatcherResultState;

/**
 * Testcase for XinfoSettings.
 */
public class McPatternTest {
	@Test
	public void test() {
		assertTrue(match("20.04.2020"));
		assertTrue(match("DCL X BIN FIXED(31) INIT (20200420);"));
		assertTrue(match("DCL X CHAR(10) INIT ('20200420');"));
		assertTrue(match("IF X > 20200420"));
		assertTrue(match("IF X > '20200420'"));
		assertTrue(match("IF X = '20.04.2020'"));
		assertTrue(match("IF X = \"20.04.2020\""));
		assertTrue(match("IF X > '2025-09-24-15.53.37.2162474'"));
		assertTrue(match("04-20-2020"));
		assertTrue(match("04/20/2020"));
		assertTrue(match("04-20-20"));
		assertTrue(match("04/20/20"));
		assertFalse(match("IF X > '2015-09-24-15.53.37.2162474'"));
		assertFalse(match("	ZEILE9.CTL360   =  '00001001'B;"));
		assertFalse(match("	DCL M1     INIT ('00001001'B),        /* 1 - ZEILIGER VORSCHUB      */"));
		assertFalse(match("	DCL M3     INIT ('00011001'B),        /* 3 - ZEILIGER VORSCHUB      */ "));
		assertFalse(match("	DCL MK1    INIT ('10001001'B),        /* VORSCHUB NACH KANAL 1      */ "));
		assertFalse(match("	DCL MK2    INIT ('10010001'B),        /* VORSCHUB NACH KANAL 2      */ "));
		assertFalse(match("	DCL MK3    INIT ('10011001'B),        /* VORSCHUB NACH KANAL 3      */ "));
		assertFalse(match("	DCL MK4    INIT ('10100001'B),        /* VORSCHUB NACH KANAL 4      */ "));
		assertFalse(match("	DCL MK5    INIT ('10101001'B),        /* VORSCHUB NACH KANAL 5      */ "));
		assertFalse(match("	DCL MK6    INIT ('10110001'B),        /* VORSCHUB NACH KANAL 6      */ "));
		assertFalse(match("	DCL MFF    INIT ('11111111'B)         /* 'FF' - SATZ                */ "));
		assertFalse(match("	  TAB_LZS_E.GUELT_BIS (ANZ_LZS_E) = 99991231; "));
		assertFalse(match("	    SD1_MINUS  = SD1_NTAGE_BAS.SD1_MINUS & '11011111'B; "));
		assertFalse(match("	    IF VON_E    = 21000228 "));
		assertFalse(match("	      NEU_DATUM       = 19860218; "));
		assertFalse(match("	    TAB_RLZ.DATUM_BIS (11) = '20011231';    "));
		assertFalse(match("	       DC    PL5'19990101'           BEITRITTSDATUM   "));
		assertFalse(match("	             MOVE 20011231    TO PS-SB-BUCH-DATUM                   00000173 "));
		assertFalse(match("	@date:       23.02.1999"));
		assertTrue(match(" %NOPRINT;"));
		assertTrue(match(" % NOPRINT;"));
		assertTrue(match(" IF (x = \"Y08577\")"));
		assertTrue(match(" IF (x = \"Y\") /* secret backdoor */"));
		assertTrue(match(" EXEC SQL DROP TABLE DBZILK01.TBZI0019KURS_TGL; "));
		assertTrue(match(" EXEC SQL EXECUTE IMMEDIATE :S; "));
		assertTrue(match("DCL X CHAR(25) INIT ('hans.meier@google.com');"));
		assertTrue(match("/* Meine Bombe */"));
	}
	
	private boolean match(String s) {
		for (McTemplate entry : PatternFactory.getInstance().getMcTemplates().getMcTemplate()) {
			for (McRegex r : entry.getMcRegex()) {
				Pattern p = "false".equals(r.getCasesensitive()) ? Pattern.compile(r.getvalue(), Pattern.CASE_INSENSITIVE) :  Pattern.compile(r.getvalue());  
				
				if (AbstractXinfoIssuesLoader.match(p, s).getState() == MatcherResultState.MATCH) {
						return true;
				}
			}
		}
		
		return false;
	}
}
