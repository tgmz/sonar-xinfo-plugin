package de.tgmz.sonar.plugins.xinfo;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;

/**
 * Generates assembler-rules.xml-
 */
public class CreateCobolRules {
	public static void main(String[] args) throws Exception {
		new CreateCobolRules().perform();
	}
	
	private void perform() throws Exception {
		PrintWriter pw = new PrintWriter("src/main/resources/cobol-rules.xml", StandardCharsets.UTF_8.name());
		
		pw.println("<xinfo-rules>");
		
		JAXBContext jaxbContext = JAXBContext.newInstance(SonarRule.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

		List<String> l = IOUtils.readLines(new FileInputStream("ibm/cobol/ErrMsg.txt"), StandardCharsets.UTF_8);
		
		StringBuilder sb = new StringBuilder();
		
		for (String s0 : l) {
			if (!(s0.startsWith("1PP 5655-S71."))) {	// Page header
				sb.append(s0);
				sb.append(' ');
			}
		}
		
		String s = sb.toString();
		
		Pattern p = Pattern.compile("IGYXX\\d{4}\\-[IWESU]");
		
		Matcher m = p.matcher(s);
		
		m.find();
		int sta = m.start();
		
		while (m.find()) {
			int end = m.start();
			String msg = s.substring(sta, end);
			
			String key = msg.substring(0, 11);
			String sev = msg.substring(10, 11);
			
			SonarRule r = new SonarRule();
			r.setCardinality("SINGLE");
			r.setInternalKey(key);
			r.setKey(key);
			r.setStatus("READY");
			r.setTag(Collections.singletonList("xinfo"));
			
			String name = s.substring(sta + 16, end).trim();
			
			r.setName(name.substring(0, Math.min(name.length(), 200))); // VARCHAR(200) in DB
			r.setDescription(name);
			
			switch (r.getKey()) {
			default:
				switch (sev) {
				case "I": r.setSeverity("MINOR"); break;
				case "W": r.setSeverity("MAJOR"); break;
				case "E": r.setSeverity("CRITICAL"); break;
				default: r.setSeverity("BLOCKER"); break;
				}
			}
			
			jaxbMarshaller.marshal(r, pw);
			
			pw.println();
			
			sta = end;
		}
		
		pw.println("</xinfo-rules>");
		
		pw.close();
	}
}
