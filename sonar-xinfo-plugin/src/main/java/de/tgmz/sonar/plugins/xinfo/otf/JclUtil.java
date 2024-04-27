/*******************************************************************************
  * Copyright (c) 03.03.2024 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.otf;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.tgmz.sonar.plugins.xinfo.AbstractXinfoProvider;

public abstract class JclUtil extends AbstractXinfoProvider {
	private JclUtil() {
	}

	public static String formatJcl(String jcl) {
		Stream<String> lines = jcl.lines();
			
		return lines.filter(x -> !x.startsWith("//*")).collect(Collectors.joining(System.lineSeparator()));
	}
}
