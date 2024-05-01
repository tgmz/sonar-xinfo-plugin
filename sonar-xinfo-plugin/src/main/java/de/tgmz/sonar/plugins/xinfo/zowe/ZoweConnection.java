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
package de.tgmz.sonar.plugins.xinfo.zowe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Configuration;

import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.config.XinfoOtfConfig;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.otf.IConnectable;
import de.tgmz.sonar.plugins.xinfo.otf.IJob;
import zowe.client.sdk.core.ZosConnection;
import zowe.client.sdk.rest.Response;
import zowe.client.sdk.rest.exception.ZosmfRequestException;
import zowe.client.sdk.zosfiles.dsn.input.CreateParams;
import zowe.client.sdk.zosfiles.dsn.input.DownloadParams;
import zowe.client.sdk.zosfiles.dsn.methods.DsnCreate;
import zowe.client.sdk.zosfiles.dsn.methods.DsnDelete;
import zowe.client.sdk.zosfiles.dsn.methods.DsnGet;
import zowe.client.sdk.zosfiles.dsn.methods.DsnWrite;
import zowe.client.sdk.zosjobs.methods.JobDelete;
import zowe.client.sdk.zosjobs.methods.JobMonitor;
import zowe.client.sdk.zosjobs.methods.JobSubmit;
import zowe.client.sdk.zosjobs.response.Job;
import zowe.client.sdk.zosjobs.types.JobStatus.Type;

public class ZoweConnection implements IConnectable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZoweConnection.class);
	private static final Random RANDOM = new SecureRandom();

	private ZosConnection connection;
	
	private Response response;
	private DsnCreate dsnCreate;
	private DsnGet dsnGet;
	private DsnWrite dsnWrite;
	private JobSubmit jobSubmit;
	private JobMonitor jobMonitor;
	private DsnDelete dsnDelete;
	private JobDelete jobDelete;

	public ZoweConnection(Configuration configuration) {
		connection = new ZosConnection(configuration.get(XinfoOtfConfig.XINFO_OTF_SERVER).orElseThrow()
				, configuration.get(XinfoOtfConfig.XINFO_OTF_PORT).orElseThrow()
				, configuration.get(XinfoOtfConfig.XINFO_OTF_USER).orElseThrow()
				, configuration.get(XinfoOtfConfig.XINFO_OTF_PASS).orElseThrow());
		
		dsnCreate = new DsnCreate(connection);
		dsnWrite = new DsnWrite(connection);
		jobSubmit = new JobSubmit(connection);
		jobMonitor = new JobMonitor(connection);
		dsnDelete = new DsnDelete(connection);
		jobDelete = new JobDelete(connection);
		dsnGet = new DsnGet(connection);
	}
	
	@Override
	public IJob submit(String jcl) throws XinfoException {
        try {
			Job job = jobSubmit.submitByJcl(jcl, null, null);

			job = jobMonitor.waitByStatus(job, Type.OUTPUT);
			
			return new JobWrapper(job);
		} catch (ZosmfRequestException e) {
        	throw new XinfoException("Job failed", e);
		}
	}

	@Override
	public byte[] retrieve(String dsn) throws XinfoException {
        DownloadParams params = new DownloadParams.Builder().build();

        try (InputStream is = dsnGet.get(dsn, params);
        		ByteArrayOutputStream os = new ByteArrayOutputStream()) {
        	IOUtils.copy(is, os);
        	
        	return os.toByteArray();
        } catch (IOException | ZosmfRequestException e) {
        	throw new XinfoException(String.format("Cannot retrieve %s", dsn), e);
		}
	}

	@Override
	public void write(String dsn, String content) throws XinfoException {
		try {
			response = dsnWrite.write(dsn, content.replaceAll("\\r\\n?", "\n"));
			LOGGER.debug("Result write {}", response);
		} catch (ZosmfRequestException e) {
        	throw new XinfoException(String.format("Cannot write %s", dsn), e);
		}
	}

	@Override
	public void deleteDsn(String dsn) throws XinfoException {
		try {
			response = dsnDelete.delete(dsn);
			LOGGER.debug("Result delete {}", response);
		} catch (ZosmfRequestException e) {
        	throw new XinfoException(String.format("Cannot delete %s", dsn), e);
		}

	}

	@Override
	public String createInputDataset(Language lang) throws XinfoException {
		String inputDsn = connection.getUser() + ".XINFO.T" + RANDOM.nextInt(10_000_000) + ".INPUT";
		
		try {
			response = dsnCreate.create(inputDsn, sequential(lang));
			LOGGER.debug("Result create {}", response);
		} catch (ZosmfRequestException e) {
        	throw new XinfoException(String.format("Cannot create %s", inputDsn), e);
		}
		
		return inputDsn;
	}

	@Override
	public String computeSysxml() {
		return connection.getUser() + ".XINFO.T" + RANDOM.nextInt(10_000_000) + ".XML";
	}
	
	private static CreateParams sequential(Language lang) {
		return new CreateParams.Builder()
                .dsorg("PS")
                .alcunit("TRK")
                .primary(10)
                .secondary(10)
                .recfm("FB")
                .lrecl(lang == Language.C || lang == Language.CPP ? 120 : 80)
                .build();
	}

	@Override
	public void deleteJob(IJob job) throws XinfoException {
		try {
			jobDelete.deleteByJob(new Job.Builder().jobId(job.getId()).jobName(job.getName()).build(), "2.0");
		} catch (ZosmfRequestException e) {
        	throw new XinfoException(String.format("Cannot delete job %s", job), e);
		}
	}

}
