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
package de.tgmz.sonar.plugins.xinfo.zowe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Configuration;

import de.tgmz.sonar.plugins.xinfo.AbstractXinfoProvider;
import de.tgmz.sonar.plugins.xinfo.XinfoException;
import de.tgmz.sonar.plugins.xinfo.config.XinfoFtpConfig;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;
import de.tgmz.sonar.plugins.xinfo.languages.Language;
import zowe.client.sdk.core.ZosConnection;
import zowe.client.sdk.rest.Response;
import zowe.client.sdk.rest.exception.ZosmfRequestException;
import zowe.client.sdk.zosfiles.dsn.input.DownloadParams;
import zowe.client.sdk.zosfiles.dsn.methods.DsnDelete;
import zowe.client.sdk.zosfiles.dsn.methods.DsnGet;
import zowe.client.sdk.zosjobs.methods.JobDelete;
import zowe.client.sdk.zosjobs.methods.JobMonitor;
import zowe.client.sdk.zosjobs.methods.JobSubmit;
import zowe.client.sdk.zosjobs.response.Job;
import zowe.client.sdk.zosjobs.types.JobStatus.Type;

/**
 * Loads issues "on-the-fly", i.e. with no stored XINFO files by invoking the appropriate compiler. 
 */
public class XinfoZoweProvider extends AbstractXinfoProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(XinfoZoweProvider.class);
	private static final Random RANDOM = new SecureRandom();

	private ZosConnection client;

	public XinfoZoweProvider(Configuration configuration) {
		super(configuration);
		
		client = new ZosConnection(configuration.get(XinfoFtpConfig.XINFO_OTF_SERVER).orElseThrow()
				, configuration.get(XinfoFtpConfig.XINFO_OTF_PORT).orElseThrow()
				, configuration.get(XinfoFtpConfig.XINFO_OTF_USER).orElseThrow()
				, configuration.get(XinfoFtpConfig.XINFO_OTF_PASS).orElseThrow());
	}

	/**
	 * The commons-net ftp client is not thread safe so we cannot run multiple tasks simultaneously.
	 */
	@Override
	public PACKAGE getXinfo(InputFile pgm) throws XinfoException {
		try {
			String sysxmlsd = client.getUser() + ".XINFO.T" + RANDOM.nextInt(10_000_000) + ".XML";

			String jcl = createJcl(pgm, sysxmlsd);

	        JobSubmit jobSubmit = new JobSubmit(client);
	        
	        Job job = jobSubmit.submitByJcl(jcl, null, null);

	        JobMonitor jobMonitor = new JobMonitor(client);
	        
	        job = jobMonitor.waitByStatus(job, Type.OUTPUT);

			byte[] xinfo = retrieveXinfo(sysxmlsd);
		
			cleanup(sysxmlsd, job);

			return createXinfo(pgm, xinfo);
		} catch (ZosmfRequestException | IOException e) {
			throw new XinfoException("Error in communication", e);
		}
	}

	private void cleanup(String sysxmlsd, Job submitJob) throws ZosmfRequestException {
        DsnDelete zosDsn = new DsnDelete(client);
        Response delete = zosDsn.delete(sysxmlsd);
        
        LOGGER.debug("Response {}", delete);
        
        JobDelete zosJob = new JobDelete(client);
        delete = zosJob.deleteByJob(submitJob, "2.0");
        
        LOGGER.debug("Response {}", delete);
	}
	
	private PACKAGE createXinfo(InputFile pgm, byte[] xinfo) throws IOException, XinfoException {
		try (InputStream is = new ByteArrayInputStream(xinfo)) {
			Language lang = Language.getByFilename(pgm.filename());
			
			return (lang == Language.C || lang == Language.CPP) ? super.createXinfoFromEvent(is) : super.createXinfo(is);
		}
	}

	private byte[] retrieveXinfo(String sysxmlsd) throws IOException, ZosmfRequestException {
        DownloadParams params = new DownloadParams.Builder().build();

        try (InputStream is = new DsnGet(client).get(sysxmlsd, params);
        		ByteArrayOutputStream os = new ByteArrayOutputStream()) {
        	IOUtils.copy(is, os);
        	
        	return os.toByteArray();
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
			
				Stream<String> lines = MessageFormat.format(s
						, getConfiguration().get(XinfoFtpConfig.XINFO_OTF_JOBCARD).orElseThrow()
						, FilenameUtils.removeExtension(inputFile.filename()).toUpperCase(Locale.getDefault())
						, inputFile.contents()
						, sysxmlsd
						, getConfiguration().get(XinfoFtpConfig.XINFO_OTF_SYSLIB).orElseThrow()).lines();
				
				return lines.filter(x -> !x.startsWith("//*")).collect(Collectors.joining(System.lineSeparator()));
			}
	}
}
