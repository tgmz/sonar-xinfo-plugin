/*******************************************************************************
  * Copyright (c) 26.03.2024 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.measures;

import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.measures.Metric;

public abstract class ComputeMetricAverage implements MeasureComputer {
	private Metric<Integer> metric;

	protected ComputeMetricAverage(Metric<Integer> metric) {
		this.metric = metric;
	}

	@Override
	public MeasureComputerDefinition define(MeasureComputerDefinitionContext def) {
		return def.newDefinitionBuilder()
				.setOutputMetrics(metric.key())
				.build();
	}

	@Override
	public void compute(MeasureComputerContext context) {
		// measure is already defined on files in scanner stack
		if (context.getComponent().getType() != Component.Type.FILE) {
			int sum = 0;
			int count = 0;
			
			for (Measure child : context.getChildrenMeasures(metric.key())) {
				sum += child.getIntValue();
				count++;
			}
			
			int average = count == 0 ? 0 : sum / count;
			
			context.addMeasure(metric.key(), average);
		}
	}
}
