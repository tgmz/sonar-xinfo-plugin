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
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Configuration;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.tgmz.sonar.plugins.xinfo.config.XinfoProjectConfig;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.FILE;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.MESSAGE;
import de.tgmz.sonar.plugins.xinfo.generated.plicomp.PACKAGE;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Fundamental implementation of a XinfoProvider.
 */
public abstract class AbstractXinfoProvider implements IXinfoProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractXinfoProvider.class);
	private static final String PROLOGUE = "<?xml version='1.0' encoding='IBM-01141'?>\n<!DOCTYPE plicomp SYSTEM 'plicomp.dtd'>\n";
	private DocumentBuilder documentBuilder;
	private Unmarshaller unmarshaller;
	private Configuration configuration;

	protected AbstractXinfoProvider() {
		try {
			documentBuilder = SecureDocumentBuilderFactory.getInstance().getDocumentBuilder();

			JAXBContext jaxbContext = JAXBContext.newInstance(PACKAGE.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			throw new XinfoRuntimeException("Error in setup", e);
		}
	}

	protected AbstractXinfoProvider(Configuration configuration) {
		this();
		
		this.configuration = configuration;
	}

	//Parser may not be thread safe.
	protected synchronized PACKAGE createXinfo(InputStream is) throws XinfoException {
		PACKAGE p = new de.tgmz.sonar.plugins.xinfo.generated.plicomp.ObjectFactory().createPACKAGE();
		
		// Perhaps we need to read the InputStream more than once so we save it to a ByteArray first
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			IOUtils.copy(is, baos);
			
			is.close();
		} catch (IOException e) {
			throw new XinfoException("I/O Error reading XINFO", e);
		}
		
		byte[] buf = baos.toByteArray();
		
		if (buf.length == 0) {
			LOGGER.warn("Xinfo is empty");
			
			return p;
		}
		
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
				
				String c = configuration.get(XinfoProjectConfig.XINFO_ENCODING).orElse(Charset.defaultCharset().name());
				
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
	protected PACKAGE createXinfoFromEvent(InputStream is) throws IOException {
		de.tgmz.sonar.plugins.xinfo.generated.plicomp.ObjectFactory of = new de.tgmz.sonar.plugins.xinfo.generated.plicomp.ObjectFactory();
		
		PACKAGE ccomp = of.createPACKAGE();
		ccomp.setFILEREFERENCETABLE(of.createFILEREFERENCETABLE());

		List<String> readLines = IOUtils.readLines(is, Charset.defaultCharset());
		
		Deque<FILE> fileStack = new LinkedList<>();
		
		for (String line : readLines) {
			// See https://www.ibm.com/docs/en/SSLTBW_2.3.0/pdf/cbcux01_v2r3.pdf appendix e
			// page 671 for the detailed format of the SYSEVENT file
			if (line.startsWith("ERROR")) {
				// The ERROR field looks like this:
				// ERROR 0 1 0 0 3 3 0 0 CCNnnnn E 12 26 Undeclared identifier add.
				//       | | | | | | | | |       | |  |  |
				//       A B C D E F G H I       J K  L  M
				String[] s = line.split("\\s", 14);
				
				MESSAGE m = of.createMESSAGE();

				m.setMSGFILE(s[2]);						// B: Increments starting with 1 for the primary file
				m.setMSGLINE(s[5]);						// E: The source line number for which the message was issued.
				m.setMSGNUMBER(s[9] + s[10]);			// I: String Containing the message identifier
														// J: Message severity character (I/W/E/S/U) (processed in AbstractXinfoIssuesLoader)
				m.setMSGTEXT(s[13]);					// M: String containing message text
				
				ccomp.getMESSAGE().add(m);
			}
			if (line.startsWith("FILEID")) {
				// The FILEID field looks like this:
				// FILEID 0 1 0 10 ./simple.c
				//        | | | |  |
				//        A B C D  E
				String[] s = line.split("\\s", 6);
				
				FILE f = of.createFILE();
				f.setFILENUMBER(s[2]);					// B: Increments starting with 1 for the primary file
				f.setINCLUDEDONLINE(s[3]);				// C: The line number of the #include directive. For the primary source file this value is 0
				f.setFILENAME(s[5])	;					// E: String containing file/dataset name.
				
				fileStack.push(f);
			}
			if (line.startsWith("FILEEND")) {
				// The FILEEND field looks like this:
				// FILEID 0 1 0
				//        | | |
				//        A B C							// B: File number that has been processed to end of line
				String[] s = line.split("\\s", 4);
				
				FILE f = fileStack.pop();
				
				// Failsafe
				if (!s[2].equals(f.getFILENUMBER())) {
					LOGGER.warn("Filestack corrupted: Expected filenumer is {} but was {}", f.getFILENUMBER(), s[2]);
				}
				
				// When the main file is processed to the end the file stack is empty
				if (!fileStack.isEmpty()) {
					f.setINCLUDEDFROMFILE(fileStack.peek().getFILENUMBER());
				}
			
				ccomp.getFILEREFERENCETABLE().getFILE().add(f);
			}
		}
		
		ccomp.getFILEREFERENCETABLE().setFILECOUNT(String.valueOf(ccomp.getFILEREFERENCETABLE().getFILE().size()));
		
		return ccomp;
		
	}

	protected Configuration getConfiguration() {
		return configuration;
	}
}
