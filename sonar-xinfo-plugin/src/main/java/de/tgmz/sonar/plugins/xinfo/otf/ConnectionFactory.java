/*******************************************************************************
  * Copyright (c) 29.04.2024 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.otf;

import org.sonar.api.config.Configuration;

import de.tgmz.sonar.plugins.xinfo.config.XinfoOtfConfig;
import de.tgmz.sonar.plugins.xinfo.ftp.FtpConnection;
import de.tgmz.sonar.plugins.xinfo.zowe.ZoweConnection;

public final class ConnectionFactory {
	private static IConnectable connection;

	private ConnectionFactory() {
		// Empty private constructor to hide the implicit public one
	}
	
	public static synchronized IConnectable getConnactable(Configuration configuration) {
		if (connection == null) {
			switch (configuration.get(XinfoOtfConfig.XINFO_OTF).orElseThrow()) {
			case "ftp":
				connection = new FtpConnection(configuration);
				break;
			case "zowe":
			default:
				connection = new ZoweConnection(configuration);
				break;
			}
		}
		
		return connection;
	}
}
