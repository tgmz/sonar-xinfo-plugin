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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import de.tgmz.sonar.plugins.xinfo.generated.plicomp.FILE;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.FILEREFERENCETABLE;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.sensors.XinfoUtil;

/**
 * Testcases for XinfoUtil.
 */
public class XinfoUtilTest {
	private static final de.tgmz.sonar.plugins.xinfo.generated.plicomp.ObjectFactory OF = new de.tgmz.sonar.plugins.xinfo.generated.plicomp.ObjectFactory();
	
	@Test(expected=XinfoException.class)
	public void testCannotCompute() throws XinfoException  {
		XinfoUtil.computeFilefromFileNumber(OF.createFILEREFERENCETABLE(), "0");
	}
	
	@Test
	public void testComputeFilefromFileNumber() throws XinfoException  {
		FILEREFERENCETABLE frt = OF.createFILEREFERENCETABLE();
		
		frt.setFILECOUNT("2");
		
		FILE f1 = OF.createFILE();
		FILE f2 = OF.createFILE();
		
		f1.setFILENAME("foo");
		f1.setFILENUMBER("1");
		
		f2.setFILENAME("bar");
		f2.setFILENUMBER("2");
		
		frt.getFILE().add(f1);
		frt.getFILE().add(f2);

		assertEquals(f1, XinfoUtil.computeFilefromFileNumber(frt, "1"));

		assertEquals(f2, XinfoUtil.computeFilefromFileNumber(frt, "2"));
	}
	
	@Test
	public void testComputeFilefromFileNumber2() throws XinfoException  {
		FILEREFERENCETABLE frt = OF.createFILEREFERENCETABLE();
		
		frt.setFILECOUNT("3");
		
		FILE f1 = OF.createFILE();
		FILE f2 = OF.createFILE();
		FILE f3 = OF.createFILE();
		
		f1.setFILENAME("foo");
		f1.setFILENUMBER("1");
		
		f2.setFILENAME("bar");
		f2.setFILENUMBER("2");
		f2.setINCLUDEDFROMFILE("1");
		f2.setINCLUDEDONLINE("11");
		
		f3.setFILENAME("foobar");
		f3.setFILENUMBER("3");
		f3.setINCLUDEDFROMFILE("2");
		f3.setINCLUDEDONLINE("7");
		
		frt.getFILE().add(f1);
		frt.getFILE().add(f2);
		frt.getFILE().add(f3);

		assertEquals("11", XinfoUtil.computeIncludedFromLine(frt, f3, Language.COBOL));
	}
	
	@Test
	public void testMainFile() throws XinfoException  {
		assertEquals("0", XinfoUtil.getMainFileNumber(Language.PLI));
		
		assertEquals("1", XinfoUtil.getMainFileNumber(Language.COBOL));
		
		assertNotEquals("1", XinfoUtil.getMainFileNumber(Language.PLI));
		
		assertNotEquals("2", XinfoUtil.getMainFileNumber(Language.COBOL));
	}
}
