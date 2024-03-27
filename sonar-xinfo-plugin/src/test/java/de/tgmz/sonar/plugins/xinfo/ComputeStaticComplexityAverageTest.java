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
package de.tgmz.sonar.plugins.xinfo;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.Collections;

import org.junit.Test;
import org.junit.Test.None;
import org.mockito.Mockito;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerContext;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerDefinition;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerDefinition.Builder;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerDefinitionContext;

import de.tgmz.sonar.plugins.xinfo.measures.ComputeDynamicComplexityAverage;
import de.tgmz.sonar.plugins.xinfo.measures.ComputeStaticComplexityAverage;

public class ComputeStaticComplexityAverageTest {
	private MeasureComputer csca = new ComputeStaticComplexityAverage();
	private MeasureComputer cdca = new ComputeDynamicComplexityAverage();
	
	@Test
	public void testDefine() {
		Builder builder = Mockito.mock(Builder.class);
		Mockito.when(builder.setOutputMetrics(anyString())).thenReturn(builder);
		Mockito.when(builder.build()).thenReturn(Mockito.mock(MeasureComputerDefinition.class));
		
		MeasureComputerDefinitionContext defContext = Mockito.mock(MeasureComputerDefinitionContext.class);
		Mockito.when(defContext.newDefinitionBuilder()).thenReturn(builder);
		
		assertNotNull(csca.define(defContext));
		assertNotNull(cdca.define(defContext));
	}

	@Test(expected = None.class)
	public void testCompute() {
		MeasureComputerContext mcc = Mockito.mock(MeasureComputerContext.class);
		Component component = Mockito.mock(Component.class);
		Mockito.when(component.getType()).thenReturn(Component.Type.PROJECT);
		
		Mockito.when(mcc.getComponent()).thenReturn(component);
		Mockito.when(mcc.getChildrenMeasures(anyString())).thenReturn(Collections.singletonList(Mockito.mock(Measure.class)));
		
		csca.compute(mcc);
		cdca.compute(mcc);
	}
}
