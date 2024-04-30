/*******************************************************************************
  * Copyright (c) 30.04.2024 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.zowe;

import de.tgmz.sonar.plugins.xinfo.otf.IJob;
import zowe.client.sdk.zosjobs.response.Job;

public class JobWrapper implements IJob {
	private Job zoweJob;
	
	public JobWrapper(Job zoweJob) {
		super();
		this.zoweJob = zoweJob;
	}

	@Override
	public String getName() {
		return zoweJob.getJobName().orElseThrow();
	}

	@Override
	public String getHandle() {
		return zoweJob.getJobId().orElseThrow();
	}
}
