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

import java.io.IOException;

import org.sonar.api.batch.fs.InputFile;

import de.tgmz.sonar.plugins.xinfo.color.assembler.AssemblerColorizing;
import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Sensor for syntaxhighlighting of HL Assembler programes.
 */
public class AssemblerColorizer extends AbstractColorizer<AssemblerColorizing> {

	public AssemblerColorizer() {
		super(Language.ASSEMBLER);
	}

	@Override
	protected AssemblerColorizing getColorizing(InputFile f, int limit) throws IOException {
		return new AssemblerColorizing(f, limit);
	}
}
