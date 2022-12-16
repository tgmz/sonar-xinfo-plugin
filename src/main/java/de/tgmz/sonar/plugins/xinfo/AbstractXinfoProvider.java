/*******************************************************************************
  * Copyright (c) 09.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.tgmz.sonar.plugins.xinfo.config.XinfoConfig;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Fundamental implementation of a XinfoProvider.
 */
public abstract class AbstractXinfoProvider implements IXinfoProvider {
	private static final Logger LOGGER = Loggers.get(AbstractXinfoProvider.class);
	private static final String PROLOGUE = "<?xml version='1.0' encoding='IBM-01141'?>\n<!DOCTYPE plicomp SYSTEM 'plicomp.dtd'>\n";
	private DocumentBuilder documentBuilder;
	private Unmarshaller unmarshaller;
	private Configuration configuration;

	public AbstractXinfoProvider() {
		try {
			documentBuilder = SecureDocumentBuilderFactory.getInstance().getDocumentBuilder();

			JAXBContext jaxbContext = JAXBContext.newInstance(PACKAGE.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (ParserConfigurationException | JAXBException e) {
			throw new XinfoRuntimeException("Error in setup", e);
		}
	}

	public AbstractXinfoProvider(Configuration configuration) {
		this();
		
		this.configuration = configuration;
	}

	@SuppressFBWarnings(value="XXE_DOCUMENT", justification="Not possible due to DocumentBuilderFactory settings")
	public PACKAGE createXinfo(InputStream is) throws XinfoException {
		PACKAGE p = null;
		
		// Perhaps we need to read the InputStream more than once so we save it to a ByteArray first
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			IOUtils.copy(is, baos);
			
			is.close();
		} catch (IOException e) {
			String msg = "I/O Error reading XINFO";
			
			LOGGER.error(msg);
			
			throw new XinfoException(msg, e);
		}
		
		byte[] buf = baos.toByteArray();
		
		Document doc;
		
		try {
			doc = documentBuilder.parse(new ByteArrayInputStream(buf));
		} catch (UnsupportedEncodingException | CharConversionException | SAXException e) {
			// Problems on z/OS: 
			// - Enterprise PL/I for z/OS and IBM Java for z/OS use different
			//   format for codepages, e.g. "IBM-1141" instead of "IBM01141" and so the XINFO 
			//   prologue <?xml version="1.0" encoding="IBM-1141"?> produces a 
			//   UnsupportedEncodingException when parsed on z/OS with the IBM JRE. No joke!
			//   Therefore we fall back from InputStream to InputSource. In this case 
			//   XERCES ignores the encoding in the Prologue.
			// - COBOL and Assembler do not include a prologue. This causes a CharConversionException 
			//   if xinfo is EBCDIC-encoded
			try {
				LOGGER.debug("Exception \"{}\" occurred, retrying", e.getMessage());
				
				String c = configuration.get(XinfoConfig.XINFO_ENCODING).orElse(Charset.defaultCharset().name());
				
				String xml = IOUtils.toString(new ByteArrayInputStream(buf), c != null ? Charset.forName(c) : Charset.defaultCharset()).trim();

				// Include the default prologue if necessary
				// This helps if xml contains non-UTF-8 characters which may occur with assembler
				if (!xml.startsWith("<?xml")) {
					xml = PROLOGUE + xml;
				}
				
				// 2nd problem: For some reason the compiler generates
				// "<ÜDOCTYPE" or "<|DOCTYPE" instead of "<!DOCTYPE".
				// Must do this, don't know why :-)
				xml = xml.replace("<ÜDOCTYPE", "<!DOCTYPE").replace("<|DOCTYPE", "<!DOCTYPE");
				
				try (Reader isr = new StringReader(xml)) {
					doc = documentBuilder.parse(new InputSource(isr));
				}
				
				LOGGER.debug("Exception \"{}\" remedied", e.getMessage());
			} catch (IOException | SAXException e0) {
				LOGGER.error("Cannot remedy exception \"{}\", giving up", e.getMessage());
				
				throw new XinfoException("Exception on parsing XINFO", e0);
			}
		} catch (IOException e) {
			String msg = "I/O error on parsing XINFO";
			
			LOGGER.error(msg);
			
			throw new XinfoException(msg, e);
		}
		
		try {
			p = (PACKAGE) unmarshaller.unmarshal(doc);
		} catch (JAXBException e) {
			String msg = "Error unmarshalling XINFO";
			
			LOGGER.error(msg);
			
			throw new XinfoException(msg, e);
		}
		
		return p;
	}

	protected Configuration getConfiguration() {
		return configuration;
	}
}
