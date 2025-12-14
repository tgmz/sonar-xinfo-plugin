/*******************************************************************************
  * Copyright (c) 08.08.2017 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.codecoverage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.tgmz.sonar.plugins.xinfo.SecureDocumentBuilderFactory;
import de.tgmz.sonar.plugins.xinfo.generated.debugtool.codecoverage.CSECT;
import de.tgmz.sonar.plugins.xinfo.generated.debugtool.codecoverage.DTCODECOVERAGEFILE;
import de.tgmz.sonar.plugins.xinfo.generated.debugtool.codecoverage.EXECUTED;
import de.tgmz.sonar.plugins.xinfo.generated.debugtool.codecoverage.UNEXECUTED;
import de.tgmz.sonar.plugins.xinfo.generated.sonar.codecoverage.Coverage;
import de.tgmz.sonar.plugins.xinfo.generated.sonar.codecoverage.Coverage.File.LineToCover;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

/**
 * Transforms a Debug Tool code Coverage out into a Sonarqube Code Coverage Input.
 */
public class Converter {
	private static final Converter instance = new Converter();
	private static final de.tgmz.sonar.plugins.xinfo.generated.sonar.codecoverage.ObjectFactory OF 
						= new de.tgmz.sonar.plugins.xinfo.generated.sonar.codecoverage.ObjectFactory();
	private JAXBContext jaxbContext;
	
	public static Converter getInstance() {
		return instance;
	}
	
	public void convert(File root, String ext, InputStream is, OutputStream os) throws ConverterException {
		DTCODECOVERAGEFILE unmarshal;
		try {
			unmarshal = getDtcodecoveragefile(is);
		} catch (JAXBException | SAXException | IOException e) {
			throw new ConverterException("Cannot parse Debug Tool output", e);
		}
		
		Coverage cov = OF.createCoverage();
		
		de.tgmz.sonar.plugins.xinfo.generated.sonar.codecoverage.Coverage.File cf = OF.createCoverageFile();
		
		CSECT csect = unmarshal.getLOADMODULE().getCOMPILATIONUNIT().getCSECT();
		
		cf.setPath(root.toString() + File.separator + csect.getEXTNAME() + ext);
		
		for (EXECUTED ex : csect.getEXECUTED()) {
			String[] executed = ex.getValue().split("\\s");
			
			for (String s : executed) {
				LineToCover ltc = OF.createCoverageFileLineToCover();
				ltc.setLineNumber(new BigInteger(s));
				ltc.setCovered(true);
				
				cf.getLineToCover().add(ltc);
			}
		}
		
		for (UNEXECUTED unex : csect.getUNEXECUTED()) {
			String[] unexecuted = unex.getValue().split("\\s");
			
			for (String s : unexecuted) {
				LineToCover ltc = OF.createCoverageFileLineToCover();
				ltc.setLineNumber(new BigInteger(s));
				ltc.setCovered(false);
				
				cf.getLineToCover().add(ltc);
			}
		}
		
		cov.getFile().add(cf);
		
		try {
			write(os, cov);
		} catch (JAXBException | IOException e) {
			throw new ConverterException("Cannot create Sonar code coverage", e);
		}
	}

	private void write(OutputStream os, Coverage cov) throws JAXBException, IOException {
		jaxbContext = JAXBContext.newInstance(Coverage.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		marshaller.marshal(cov, os);
		
		os.close();
	}

	private DTCODECOVERAGEFILE getDtcodecoveragefile(InputStream is)
			throws JAXBException, SAXException, IOException {
		DocumentBuilder documentBuilder = SecureDocumentBuilderFactory.getInstance().getDocumentBuilder();

		jaxbContext = JAXBContext.newInstance(DTCODECOVERAGEFILE.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		
		DTCODECOVERAGEFILE unmarshal = null;
		
		Document doc = documentBuilder.parse(is);

		unmarshal = (DTCODECOVERAGEFILE) unmarshaller.unmarshal(doc);
		return unmarshal;
	}

}
