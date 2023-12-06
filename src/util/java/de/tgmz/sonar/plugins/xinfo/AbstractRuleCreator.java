/*******************************************************************************
  * Copyright (c) 18.01.2022 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.api.rule.RuleStatus;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import de.tgmz.sonar.plugins.xinfo.generated.Rule;
import de.tgmz.sonar.plugins.xinfo.generated.Tag;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

public abstract class AbstractRuleCreator {
	private static final Logger LOGGER = Loggers.get(AbstractRuleCreator.class);
	private String documentation;
	private String output;
	private PrintWriter pw;
	private Set<String> rules = new HashSet<>();
	private Marshaller jaxbMarshaller;
	
	protected AbstractRuleCreator(String documentation, String output) {
		super();
		this.documentation = documentation;
		this.output = output;
	}

	public void open() throws IOException, JAXBException {
		File outDir = new File(output).getParentFile();
		
		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		pw = new PrintWriter(output, StandardCharsets.UTF_8.name());
		
		pw.println("<xinfo-rules>");
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Rule.class);
		jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
	}
	
	public void close() {
		pw.println("</xinfo-rules>");
		
		pw.close();
	}
	
	public void createRule(String key, String name) throws JAXBException {
		createRule(key, name, name);
	}
	
	public void createRule(String key, String name, String description) throws JAXBException {
		if (rules.contains(key)) {
			LOGGER.warn("Rule {} already added", key);

			return;
		}
		
		char sev = key.charAt(key.length() - 1);
		
		Rule r = new Rule();
		r.setCardinality("SINGLE");
		r.setInternalKey(key);
		r.setKey(key);
		r.setStatus("READY");
		Tag tag = new Tag(); tag.setvalue("xinfo"); r.getTag().add(tag);
		r.setStatus(RuleStatus.READY.toString());
		r.setRemediationFunction("CONSTANT_ISSUE");
		r.setRemediationFunctionBaseEffort("0d 0h 10min");

		r.setName(name.substring(0, Math.min(name.length(), 200))); // VARCHAR(200) in DB
		r.setDescription(description);
		
		switch (sev) {
			case 'S':
			case 'U': r.setSeverity("BLOCKER"); r.setType("BUG"); break;
			case 'W': r.setSeverity("MAJOR"); break;
			case 'E': r.setSeverity("CRITICAL"); r.setType("BUG"); break;
			case 'I': 
			default: 
				r.setSeverity("MINOR"); break;
		}
		
		jaxbMarshaller.marshal(r, pw);
		
		pw.println();
		
		rules.add(key);
	}

	public String getDocumentation() {
		return documentation;
	}
	
	public List<String> getSections(String documentation, String msgPattern) {
		List<String> result = new LinkedList<>();
		
		Pattern p = Pattern.compile(msgPattern);
		
		Matcher m = p.matcher(documentation);
		
		m.find();
		int sta = m.start();
		
		while (m.find()) {
			int end = m.start();
			result.add(documentation.substring(sta, end));
			
			sta = end;
		}

		return result;
	}
}
