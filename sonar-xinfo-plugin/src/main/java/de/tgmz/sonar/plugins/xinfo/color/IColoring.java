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
package de.tgmz.sonar.plugins.xinfo.color;

/**
 * General interface for Syntax highlighting.
 */
@FunctionalInterface
public interface IColoring {
	HighligthedAreas getAreas();
}
