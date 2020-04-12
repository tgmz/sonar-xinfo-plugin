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
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.sensors.AbstractXinfoIssuesLoader;
import de.tgmz.sonar.plugins.xinfo.sensors.matcher.MatcherResult.MatcherResultState;

/**
 * Testcase for XinfoSettings.
 */
public class McPatternTest {
	private static Map<String, List<Pattern>> mcPatternListMap = new TreeMap<>();
	@BeforeClass
	public static void setupOnce() {
		for (Language l : Language.values()) {
			mcPatternListMap.putAll(PatternFactory.getInstance().getMcPatterns(l));
		}
	}
	
	@Test
	public void test() {
		assertTrue(match("20.04.2020"));
		assertTrue(match("DCL X BIN FIXED(31) INIT (20200420);"));
		assertTrue(match("DCL X CHAR(10) INIT ('20200420');"));
		assertTrue(match("IF X > 20200420"));
		assertTrue(match("IF X > '20200420'"));
		assertTrue(match("IF X = '20.04.2020'"));
		assertTrue(match("IF X = \"20.04.2020\""));
		assertTrue(match("IF X > '2007-09-24-15.53.37.2162474'"));
		assertTrue(match("04-20-2020"));
		assertTrue(match("04/20/2020"));
		assertTrue(match("04-20-20"));
		assertTrue(match("04/20/20"));
		assertTrue(match(" %IF X=Y"));
		assertTrue(match(" IF (x = \"Y08577\")"));
		assertTrue(match(" IF (x = \"Y\") /* secret backdoor */"));
		assertTrue(match(" EXEC SQL DROP TABLE DBZILK01.TBZI0019KURS_TGL; "));
		assertTrue(match(" EXEC SQL EXECUTE IMMEDIATE :S; "));
		assertTrue(match("DCL X CHAR(25) INIT ('hans.meier@google.com');"));
	}
	
	private boolean match(String s) {
		for (Entry<String, List<Pattern>> entry : mcPatternListMap.entrySet()) {
			for (Pattern p : entry.getValue()) {
				if (AbstractXinfoIssuesLoader.match(p, s).getState() == MatcherResultState.MATCH) {
						return true;
				}
			}
		}
		
		return false;
	}
}
