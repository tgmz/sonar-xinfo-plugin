/*******************************************************************************
  * Copyright (c) 11.11.2017 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.color;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Highlighted areas.
 */
public class HighligthedAreas {
	private List<ColorizingData> colorizings;

	public HighligthedAreas() {
		colorizings = new LinkedList<>();
	}
	public List<ColorizingData> getColorizings() {
		return colorizings;
	}
	public void add(ColorizingData t) {
		for (Iterator<ColorizingData> iterator = colorizings.iterator(); iterator.hasNext();) {
			if (iterator.next().overlap(t)) {
				return;
			}
		}
		
		colorizings.add(t);
	}
}
