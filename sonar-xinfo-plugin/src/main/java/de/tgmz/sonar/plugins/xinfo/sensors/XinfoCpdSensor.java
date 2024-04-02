/*******************************************************************************
  * Copyright (c) 06.07.2021 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.sensors;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.cpd.NewCpdTokens;

import de.tgmz.sonar.plugins.xinfo.config.XinfoProjectConfig;
import de.tgmz.sonar.plugins.xinfo.languages.XinfoLanguage;

/**
 * Tokenize files for CPD
 */
public class XinfoCpdSensor implements Sensor {
	private static final Logger LOGGER = LoggerFactory.getLogger(XinfoCpdSensor.class);
	
	@Override
	public void describe(SensorDescriptor descriptor) {
		descriptor.name("XinfoCpdSensor");
		descriptor.onlyOnLanguages(XinfoLanguage.KEY);
	}

	@Override
	public void execute(SensorContext context) {
		if (context.config().getBoolean(XinfoProjectConfig.XINFO_CPD_OFF).orElse(false)) {
			return;
		}
		
		FilePredicates p = context.fileSystem().predicates();
		
		Optional<Integer> numThreads = context.config().getInt(XinfoProjectConfig.XINFO_NUM_THREADS);
		
		ExecutorService es = numThreads.isPresent() ? Executors.newFixedThreadPool(numThreads.get()) : Executors.newCachedThreadPool();
		
		List<Callable<NewCpdTokens>> tasks = new LinkedList<>();
		
		for (InputFile inputFile : context.fileSystem().inputFiles(p.hasLanguages(XinfoLanguage.KEY))) {
			tasks.add(new CpdExecutor(context, inputFile));
		}
		
		LOGGER.info("Executing {} tasks", tasks.size());
		
		try {
			List<Future<NewCpdTokens>> invokeAll = es.invokeAll(tasks);

			for (Future<NewCpdTokens> fXinfo : invokeAll) {
				fXinfo.get().save();
			}
		} catch (ExecutionException e) {
			LOGGER.error("Error invoking HighlightExecutor", e);
		} catch (InterruptedException  e) {
			LOGGER.error("XinfoExecutor invocation interrupted", e);
			
			Thread.currentThread().interrupt();
		}
	}
}
