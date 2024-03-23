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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Arrays;
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

import de.tgmz.sonar.plugins.xinfo.AbstractXinfoProvider;
import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.config.XinfoFtpConfig;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;
import de.tgmz.sonar.plugins.xinfo.languages.Language;

/**
 * Loads issues "on-the-fly", i.e. with no stored XINFO files by invoking the appropriate compiler. 
 */
public class XinfoOnTheFlyProvider extends AbstractXinfoProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(XinfoOnTheFlyProvider.class);
	private static final Random RANDOM = new SecureRandom();

	// Since getXinfo() is synchronized we only need a single client
	private static JesClient client = new JesClient();

	public XinfoOnTheFlyProvider(Configuration configuration) {
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

		String sysxmlsd = user + ".XINFO.T" + RANDOM.nextInt(10_000_000) + ".XML";

		try {
			String jcl = createJcl(pgm, sysxmlsd);

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
		client.deleteFile("//" + sysxmlsd);
		client.site("FILE=Jes");
		client.deleteFile(submitJob.getHandle());
	}
	
	private PACKAGE createXinfo(InputFile pgm, byte[] xinfo) throws IOException, XinfoException {
		try (InputStream is = new ByteArrayInputStream(xinfo)) {
			return Arrays.asList(Language.C, Language.CPP).contains(Language.getByFilename(pgm.filename())) ? super.createXinfoFromEvent(is) : super.createXinfo(is);
		}
	}

	private byte[] retrieveXinfo(String sysxmlsd) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			client.site("FILE=SEQ");

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
	            client.site("FILE=Jes JesJOBNAME=*");
	            
				return true;
			} else {		
				return false;
			}
		} catch (IOException e) {
			throw new XinfoException("Connect unsuccessfull", e);
		}
	}

	private String createJcl(InputFile inputFile, String sysxmlsd) throws IOException, XinfoException {
		String template = null;

		switch (Language.getByFilename(inputFile.filename())) {
		case COBOL:
			template = "elaxfcoc.txt";
			break;
		case C:
			template = "elaxfcpc.txt";
			break;
		case CPP:
			template = "elaxfcpp.txt";
			break;
		case ASSEMBLER:
			template = "elaxfasm.txt";
			break;
		case PLI:
		default:
			template = "elaxfpl1.txt";
			break;
		}
		
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(template);
				Reader r = new InputStreamReader(is, StandardCharsets.UTF_8)) {
				String s = IOUtils.toString(r);
			
				return MessageFormat.format(s
						, getOtfValue(XinfoFtpConfig.XINFO_OTF_JOBCARD)
						, FilenameUtils.removeExtension(inputFile.filename()).toUpperCase(Locale.getDefault())
						, inputFile.contents()
						, sysxmlsd);
			}
	}

	private JesJob findJob(final List<JesJob> list, final JesJob job) {
		Optional<JesJob> o = list.stream().filter(n -> job.getHandle().equals(n.getHandle())).findFirst();

		return o.isPresent() ? o.get() : job;
	}
	private String getOtfValue(String param) throws XinfoException {
		return getConfiguration().get(param).orElseThrow(() -> new XinfoException("Param " + param + " not provided"));
	}
}
