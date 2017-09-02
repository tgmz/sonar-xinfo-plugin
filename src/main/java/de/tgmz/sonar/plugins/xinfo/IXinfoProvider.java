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
package de.tgmz.sonar.plugins.xinfo;

import de.tgmz.sonar.plugins.xinfo.plicomp.PACKAGE;

/**
 * Interface for providing programinformations.
 */
@FunctionalInterface 
public interface IXinfoProvider {
	PACKAGE getXinfo(IXinfoAnalyzable pgm) throws XinfoException;
}
