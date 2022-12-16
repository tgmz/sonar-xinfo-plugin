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

import org.sonar.api.batch.fs.InputFile;

import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;

/**
 * Interface for providing programinformations.
 */
@FunctionalInterface 
public interface IXinfoProvider {
	PACKAGE getXinfo(InputFile pgm) throws XinfoException;
}
