/*******************************************************************************
  * Copyright (c) 17.12.2016 Thomas Zierer.
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
import org.sonar.api.config.internal.ConfigurationBridge;
import org.sonar.api.config.internal.MapSettings;

import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;

/**
 * Dummy to access the createXinfo(InputStream) method.
 */
class DummyXinfoProvider extends AbstractXinfoProvider {
	public DummyXinfoProvider() {
		super(new ConfigurationBridge(new MapSettings()));
	}
	@Override
	public PACKAGE getXinfo(InputFile pgm) throws XinfoException {
		return null;
	}
}