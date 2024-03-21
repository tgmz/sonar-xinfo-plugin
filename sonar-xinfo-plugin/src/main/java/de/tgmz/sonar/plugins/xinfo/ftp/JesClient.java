/*******************************************************************************
  * Copyright (c) 26.02.2024 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.ftp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * Extend FTPClient for JES interaction.
 */
public class JesClient extends FTPClient {
    public JesClient() {
		super();
		
		this.addProtocolCommandListener(new JesProtocolCommandListener());
	}

	public int setOwnerFilter(String owner) throws IOException {
        return site(String.format("JesOWNER=%s", owner));
    }

    public List<JesJob> listJobsDetailed() throws IOException {
        List<JesJob> jobs = new ArrayList<>();
        for (FTPFile file : listFiles()) {
            JesJob job = new JesJob();
            if (job.parseDetails(file.getRawListing())) {
                jobs.add(job);
            }
        }
        return jobs;
    }

    public JesJob submit(String sourceJCL) throws IOException {
        OutputStream outputStream = storeFileStream("job");
        outputStream.write(sourceJCL.getBytes());
        outputStream.close();
        completePendingCommand();

        Pattern pattern = Pattern.compile("It is known to J[Ee][Ss] as (?<handle>.+)");
        Matcher matcher = pattern.matcher(getReplyString());
        JesJob job = null;
        if (matcher.find()) {
            job = new JesJob();
            job.setHandle(matcher.group("handle"));
        }
        return job;
    }
}