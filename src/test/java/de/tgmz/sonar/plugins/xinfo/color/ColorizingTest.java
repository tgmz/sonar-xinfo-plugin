/*******************************************************************************
  * Copyright (c) 09.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/

package de.tgmz.sonar.plugins.xinfo.color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import org.junit.Test;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

import de.tgmz.sonar.plugins.xinfo.color.assembler.AssemblerColorizing;
import de.tgmz.sonar.plugins.xinfo.color.cobol.CobolColorizing;
import de.tgmz.sonar.plugins.xinfo.color.pli.PliColorizing;

public class ColorizingTest {

	@Test
	public void testPli() throws IOException {
		PliColorizing pliColorizing0 = new PliColorizing(new File("testresources/plitest.pli"), 10);
		PliColorizing pliColorizing1 = new PliColorizing(new File("testresources/plitest.pli"), 3);
		
		assertEquals(10, pliColorizing0.getAreas().size());
		
		assertEquals(TypeOfText.KEYWORD, ((TreeSet<ColorizingData>) pliColorizing0.getAreas()).first().getType());
		
		assertEquals(9, pliColorizing1.getAreas().size());
	}

	@Test
	public void testCobol() throws IOException {
		assertEquals(14, new CobolColorizing(new File("testresources/cobtest.cbl"), 10).getAreas().size());
	}

	@Test
	public void testAsm() throws IOException {
		assertEquals(17, new AssemblerColorizing(new File("testresources/asmtest.asm"), 20).getAreas().size());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test() {
		new ColorizingData(1, 1, 2, 2, "", TypeOfText.COMMENT);
	}
	
	@Test
	public void testEquals() throws IOException {
		ColorizingData cd0 = new ColorizingData(1, 1, 1, 80, "", TypeOfText.COMMENT);
		ColorizingData cd1 = new ColorizingData(1, 40, 1, 50, "", TypeOfText.COMMENT);
		ColorizingData cd2 = new ColorizingData(1, 20, 1, 30, "", TypeOfText.COMMENT);
		
		assertEquals(cd0, cd1);
		assertEquals(cd0, cd0);
		assertNotEquals(cd1, cd2);
		assertNotEquals(cd1, null);
		assertNotEquals(cd1, Integer.valueOf(0));
	}
}
