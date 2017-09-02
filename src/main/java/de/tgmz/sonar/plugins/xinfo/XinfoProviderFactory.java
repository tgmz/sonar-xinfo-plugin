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

import org.sonar.api.config.Settings;

/**
 * Singleton factory for the XinfoProvider to use.
 */
public final class XinfoProviderFactory {
	private static volatile IXinfoProvider provider;
	
	private XinfoProviderFactory() {
		// Empty private constructor to hide the implicit public one
	}
	
	public static IXinfoProvider getProvider(Settings settings) {
		if (provider == null) {
			provider = new XinfoFileProvider(settings);
		}
		
		return provider;
	}
}
