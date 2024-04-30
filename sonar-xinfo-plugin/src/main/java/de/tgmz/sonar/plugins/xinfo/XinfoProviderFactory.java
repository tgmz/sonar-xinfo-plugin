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

import java.util.Optional;

import org.sonar.api.config.Configuration;

import de.tgmz.sonar.plugins.xinfo.config.XinfoFtpConfig;
import de.tgmz.sonar.plugins.xinfo.otf.OtfProvider;

/**
 * Singleton factory for the XinfoProvider to use.
 */
public final class XinfoProviderFactory {
	private static IXinfoProvider provider;
	
	private XinfoProviderFactory() {
		// Empty private constructor to hide the implicit public one
	}
	
	public static synchronized IXinfoProvider getProvider(Configuration configuration) {
		if (provider == null) {
			Optional<String> oOtf = configuration.get(XinfoFtpConfig.XINFO_OTF);
			
			if (oOtf.isPresent() && !"off".equalsIgnoreCase(oOtf.get())) {
				provider = new OtfProvider(configuration);
			} else {
				provider = new XinfoFileProvider(configuration);
			}
		}
		
		return provider;
	}
}
