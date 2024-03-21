/*******************************************************************************
  * Copyright (c) 21.03.2024 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.sensors;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Configuration;

import de.tgmz.sonar.plugins.xinfo.IXinfoProvider;
import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.XinfoProviderFactory;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;

public class XinfoExecutor implements Callable<Map<InputFile,PACKAGE>> {
	private IXinfoProvider xinfoProvider;
	private InputFile inputFile;
	
	public XinfoExecutor(Configuration config, InputFile inputFile) {
		super();
		
		this.inputFile = inputFile;
		this.xinfoProvider = XinfoProviderFactory.getProvider(config);
	}
	
	// We need the inputFile later on so we must return it too
	@Override
	public Map<InputFile, PACKAGE> call() throws XinfoException {
		PACKAGE xinfo = xinfoProvider.getXinfo(inputFile);
		
		return Collections.singletonMap(inputFile, xinfo);
	}
}
