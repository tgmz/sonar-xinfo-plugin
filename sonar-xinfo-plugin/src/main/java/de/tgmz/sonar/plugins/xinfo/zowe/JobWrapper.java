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

import de.tgmz.sonar.plugins.xinfo.ftp.JesJob;
import de.tgmz.sonar.plugins.xinfo.otf.IJob;
import zowe.client.sdk.zosjobs.model.Job;

public class JobWrapper implements IJob {
	private Job zoweJob;
	private JesJob jesJob;
	
	public JobWrapper(Job zoweJob) {
		this.zoweJob = zoweJob;
	}

	public JobWrapper(JesJob jesJob) {
		this.jesJob = jesJob;
	}

	@Override
	public String getName() {
		if (jesJob != null) {
			return jesJob.getName();
		} else {
			return zoweJob.getJobName();
		}
	}

	@Override
	public String getId() {
		if (jesJob != null) {
			return jesJob.getHandle();
		} else {
			return zoweJob.getJobId();
		}
	}
}
