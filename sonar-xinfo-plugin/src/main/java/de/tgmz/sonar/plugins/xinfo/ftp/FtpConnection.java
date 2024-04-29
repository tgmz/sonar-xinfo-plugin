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
package de.tgmz.sonar.plugins.xinfo.ftp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Configuration;

import de.tgmz.sonar.plugins.xinfo.XinfoRuntimeException;
import de.tgmz.sonar.plugins.xinfo.config.XinfoFtpConfig;
import de.tgmz.sonar.plugins.xinfo.config.XinfoProjectConfig;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.otf.IConnectable;
import de.tgmz.sonar.plugins.xinfo.otf.OtfException;

public class FtpConnection implements IConnectable {
	private static final Logger LOGGER = LoggerFactory.getLogger(FtpConnection.class);
	private static final String TYPE_JES = "FILE=Jes";
	private static final String TYPE_SEQ = "FILE=SEQ";
	private static final Random RANDOM = new SecureRandom();
	private JesClient client;
	private Configuration configuration;

	public FtpConnection(Configuration configuration) {
		this.configuration = configuration;
		
		client = new JesClient();
		
		try {
			connect();
		} catch (IOException e) {
			throw new XinfoRuntimeException("Cannot setup JesClient", e);
		}
	}
	
	@Override
	public void submit(String jcl) throws OtfException {
		try {
			client.site(TYPE_JES);

			JesJob xinfoJob = client.submit(jcl);
			xinfoJob.setStatus("");

			client.setOwnerFilter(configuration.get(XinfoFtpConfig.XINFO_OTF_USER).orElseThrow());
			
			long start = System.currentTimeMillis();
			int timeout = configuration.getInt(XinfoFtpConfig.XINFO_OTF_TIMEOUT).orElse(10) * 1_000;

			while (System.currentTimeMillis() - start < timeout && !"OUTPUT".equals(xinfoJob.getStatus())) {
				List<JesJob> listJobs = client.listJobsDetailed();

				xinfoJob = findJob(listJobs, xinfoJob);
			}

			if (!"OUTPUT".equals(xinfoJob.getStatus())) {
				throw new OtfException("Job didn't finish");
			}
			
			LOGGER.debug("Job finished in {} msecs", System.currentTimeMillis() - start);
			LOGGER.debug("Job details: {}", xinfoJob);
		} catch (IOException e) {
			throw new OtfException("Submit failed", e);
		}
	}

	@Override
	public byte[] retrieve(String dsn) throws OtfException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			client.site(TYPE_SEQ);

			client.retrieveFile("//" + dsn, baos);

			baos.flush();
			
			return baos.toByteArray();
		} catch (IOException e) {
			throw new XinfoRuntimeException("Cannot setup JesClient", e);
		}
	}

	@Override
	public void write(String dsn, String content) throws OtfException {
		try (InputStream is = IOUtils.toInputStream(content, configuration.get(XinfoProjectConfig.XINFO_ENCODING).orElse("UTF-8"))) {
			client.site(TYPE_SEQ);
			client.storeFile("//" + dsn, is);
		} catch (IOException e) {
			throw new OtfException(String.format("Cannot write to %s", dsn), e);
		}
	}

	@Override
	public void deleteDsn(String dsn) throws OtfException {
		try {
			client.site(TYPE_SEQ);
			client.deleteFile("//" + dsn);
		} catch (IOException e) {
			throw new OtfException(String.format("Cannot delete %s", dsn), e);
		}
	}

	@Override
	public String createAndUploadInputDataset(Language lang, String content) throws OtfException {
		try {
			client.site(TYPE_SEQ);
			client.site("DSNTYPE=BASIC");
			client.site("RECFM=FB");
			client.site("LRECL=" + (lang == Language.C || lang == Language.CPP ? "120" : "80"));
			client.site("PRIMARY=100");
			client.site("SECONDARY=100");
		
			String s = configuration.get(XinfoFtpConfig.XINFO_OTF_USER).orElseThrow() + ".XINFO.T" + RANDOM.nextInt(10_000_000) + ".INPUT";
		
			try (InputStream is = IOUtils.toInputStream(content, configuration.get(XinfoProjectConfig.XINFO_ENCODING).orElse("UTF-8"))) {
				client.storeFile(s, is);
			}
		
			return s;
		} catch (IOException e) {
			throw new OtfException("Cannot create/upload input dataset", e);
		}
	}

	@Override
	public String createSysxml() throws OtfException {
		return configuration.get(XinfoFtpConfig.XINFO_OTF_USER).orElseThrow() + ".XINFO.T" + RANDOM.nextInt(10_000_000) + ".XML";
	}
	
	private void connect() throws IOException {
		int reply;
		
		client.connect(configuration.get(XinfoFtpConfig.XINFO_OTF_SERVER).orElseThrow()
					, configuration.getInt(XinfoFtpConfig.XINFO_OTF_PORT).orElse(21));

		// After connection attempt, you should check the reply code to verify
		// success.
		reply = client.getReplyCode();

		if (!FTPReply.isPositiveCompletion(reply)) {
			client.disconnect();
			throw new IOException("Connect unsuccessfull");
		}

		if (client.login(configuration.get(XinfoFtpConfig.XINFO_OTF_USER).orElseThrow()
				, configuration.get(XinfoFtpConfig.XINFO_OTF_PASS).orElseThrow())) {
			client.enterLocalPassiveMode();
            client.site(TYPE_JES + " JesJOBNAME=*");
		} else {		
			throw new IOException("Login unsuccessfull");
		}
	}
	private JesJob findJob(final List<JesJob> list, final JesJob job) {
		Optional<JesJob> o = list.stream().filter(n -> job.getHandle().equals(n.getHandle())).findFirst();

		return o.isPresent() ? o.get() : job;
	}
}
