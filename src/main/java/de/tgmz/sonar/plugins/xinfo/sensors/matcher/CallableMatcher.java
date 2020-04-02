/*******************************************************************************
  * Copyright (c) 05.03.2020 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution,  and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.sensors.matcher;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

/**
 * Patternmatcher as CallableService to prevent timeouts.
 */
public class CallableMatcher implements Callable<String>{
	private Pattern p;
	private String s;

	public CallableMatcher(Pattern p, String s) {
		super();
		this.p = p;
		this.s = s;
	}

	@Override
	public @Nullable String call() {
		Matcher m = p.matcher(s);
		
		if (m.matches()) {
			return m.group(0);
		}
		
		return null;
	}
}
