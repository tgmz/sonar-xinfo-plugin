/*******************************************************************************
  * Copyright (c) 03.03.2024 Thomas Zierer.
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
import java.util.Locale;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Configuration;

import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.config.XinfoFtpConfig;
import de.tgmz.sonar.plugins.xinfo.config.XinfoProjectConfig;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import de.tgmz.sonar.plugins.xinfo.otf.AbstractOtfProvider;

/**
 * Loads issues "on-the-fly" by using a ftp client. 
 */
public class XinfoFtpProvider extends AbstractOtfProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(XinfoFtpProvider.class);
	private static final Random RANDOM = new SecureRandom();
	private static final String TYPE_JES = "FILE=Jes";
	private static final String TYPE_SEQ = "FILE=SEQ";

	// Since getXinfo() is synchronized we only need a single client
	private static JesClient client = new JesClient();

	public XinfoFtpProvider(Configuration configuration) {
		super(configuration);
	}

	/**
	 * The commons-net ftp client is not thread safe so we cannot run multiple tasks simultaneously.
	 */
	@Override
	public synchronized PACKAGE getXinfo(InputFile pgm) throws XinfoException {
		String user = getOtfValue(XinfoFtpConfig.XINFO_OTF_USER);
		
		if (client == null || !client.isConnected()) {
			LOGGER.debug("Connecting");
			
			connect(user);
		}

		try {
			String sysxmlsd = computeXinfoDataset(user, ".XML");
			String inputDataset = computeXinfoDataset(user, ".INPUT");
			
			createInputDataset(inputDataset, pgm);
			
			String jcl = createJcl(Language.getByFilename(pgm.filename())
					, FilenameUtils.removeExtension(pgm.filename()).toUpperCase(Locale.getDefault())
					, inputDataset
					, sysxmlsd);

			client.site(TYPE_JES);

			JesJob xinfoJob = client.submit(jcl);
			xinfoJob.setStatus("");

			client.setOwnerFilter(user);
			
			long start = System.currentTimeMillis();
			int timeout = Integer.parseInt(getOtfValue(XinfoFtpConfig.XINFO_OTF_TIMEOUT)) * 1_000;

			while (System.currentTimeMillis() - start < timeout && !"OUTPUT".equals(xinfoJob.getStatus())) {
				List<JesJob> listJobs = client.listJobsDetailed();

				xinfoJob = findJob(listJobs, xinfoJob);
			}

			if (!"OUTPUT".equals(xinfoJob.getStatus())) {
				throw new XinfoException("Job didn't finish");
			}
			
			LOGGER.debug("Job finished in {} msecs", System.currentTimeMillis() - start);
			LOGGER.debug("Job details: {}", xinfoJob);
			
			if (xinfoJob.getAbendCode() != null) {
				LOGGER.error("Job abended");
				
				return null;
			}

			byte[] xinfo = retrieveXinfo(sysxmlsd);
		
			cleanup(sysxmlsd, xinfoJob);

			return createXinfo(pgm, xinfo);
		} catch (IOException e) {
			throw new XinfoException("IOException in communication", e);
		}
	}

	private void cleanup(String sysxmlsd, JesJob submitJob) throws IOException {
		client.site(TYPE_SEQ);
		client.deleteFile("//" + sysxmlsd);
		client.site(TYPE_JES);
		client.deleteFile(submitJob.getHandle());
	}
	
	private byte[] retrieveXinfo(String sysxmlsd) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			client.site(TYPE_SEQ);

			client.retrieveFile("//" + sysxmlsd, baos);

			baos.flush();
			
			return baos.toByteArray();
		}
	}
	
	private boolean connect(String user) throws XinfoException {
		int reply;
		
		try {
			client.connect(getOtfValue(XinfoFtpConfig.XINFO_OTF_SERVER)
					, Integer.parseInt(getOtfValue(XinfoFtpConfig.XINFO_OTF_PORT)));

			// After connection attempt, you should check the reply code to verify
			// success.
			reply = client.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				client.disconnect();
				throw new XinfoException("Connect unsuccessfull");
			}

			if (client.login(user, getOtfValue(XinfoFtpConfig.XINFO_OTF_PASS))) {
				client.enterLocalPassiveMode();
	            client.site(TYPE_JES + " JesJOBNAME=*");
	            
				return true;
			} else {		
				return false;
			}
		} catch (IOException e) {
			throw new XinfoException("Connect unsuccessfull", e);
		}
	}

	private JesJob findJob(final List<JesJob> list, final JesJob job) {
		Optional<JesJob> o = list.stream().filter(n -> job.getHandle().equals(n.getHandle())).findFirst();

		return o.isPresent() ? o.get() : job;
	}
	
	private String getOtfValue(String param) throws XinfoException {
		return getConfiguration().get(param).orElseThrow(() -> new XinfoException("Param " + param + " not provided"));
	}
	
	private String computeXinfoDataset(String user, String suffix) throws IOException {
		client.site(TYPE_SEQ);
		
		for (int i = 0; i < 5; ++i) {
			String sysxmlsd = user + ".XINFO.T" + RANDOM.nextInt(10_000_000) + suffix;
		
			String[] names = client.listNames("//" + sysxmlsd);
		
			if (names == null || names.length == 0) {
				return sysxmlsd;
			}
		}
		
		throw new IOException("Cannot compute SYSXMLSD dataset name");
	}
	private void createInputDataset(String s, InputFile pgm) throws IOException {
		client.site("FILETYPE=SEQ");
		client.site("DSNTYPE=BASIC");
		client.site("RECFM=FB");
		client.site("LRECL=120");
		client.site("PRIMARY=100");
		client.site("SECONDARY=100");
		
		try (InputStream is = IOUtils.toInputStream(pgm.contents(), getConfiguration().get(XinfoProjectConfig.XINFO_ENCODING).orElse("UTF-8"))) {
			client.storeFile(s, is);
		}
	}
}
