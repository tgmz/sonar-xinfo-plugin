/*******************************************************************************
  * Copyright (c) 13.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.sensors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

//DocumentMe 

public class McIssuesLoader implements Sensor {
	private static final Logger LOGGER = Loggers.get(McIssuesLoader.class);
	private final FileSystem fileSystem;
	private SensorContext context;

	public McIssuesLoader(final FileSystem fileSystem) {
		this.fileSystem = fileSystem;
	}


	@Override
	public void describe(final SensorDescriptor descriptor) {
		descriptor.name("Malicious Code Issues Loader Sensor");
		descriptor.onlyOnLanguage("pli");
	}
	
	@Override
	public void execute(final SensorContext aContext) {
		LOGGER.debug("execute");

		this.context = aContext;
		
		Iterator<InputFile> fileIterator = fileSystem.inputFiles(fileSystem.predicates().hasLanguage("pli")).iterator();

		int ctr = 0;
		
		while (fileIterator.hasNext()) {
			InputFile inputFile = fileIterator.next();
			
			try (BufferedReader br = new BufferedReader(new InputStreamReader(inputFile.inputStream()))) {
				String s;
				int i = 1;
					
				while ((s = br.readLine()) != null) {
					if (s.matches("^.*\\s+IMMEDIATE\\s+.*$")) {
						RuleKey ruleKey = RuleKey.of("mc-pli", "MC00042");

						NewIssue newIssue = context.newIssue().forRule(ruleKey);
							
						NewIssueLocation primaryLocation = newIssue.newLocation().on(inputFile).message("Dynamic query");
						primaryLocation.at(inputFile.selectLine(i));
							
						newIssue.at(primaryLocation).save();
					}
					
					++i;
				}
			} catch (IOException e) {
				LOGGER.error("IOException on {}", inputFile.filename());
			}
			
			if (++ctr % 100 == 0) {
				LOGGER.info("{} files processed, current is {}", ctr, inputFile.filename());
			}
		}
	}
}
