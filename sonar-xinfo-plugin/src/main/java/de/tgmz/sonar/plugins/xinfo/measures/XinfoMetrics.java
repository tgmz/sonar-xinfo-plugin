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

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

public class XinfoMetrics implements Metrics {

  public static final Metric<Integer> STATIC_COMPLEXITY = new Metric.Builder("static_complexity", "Static complexity", Metric.ValueType.INT)
    .setDescription("Number of referenced files")
    .setDirection(Metric.DIRECTION_NONE)
    .setQualitative(false)
    .setDomain(CoreMetrics.DOMAIN_COMPLEXITY)
    .create();

  public static final Metric<Integer> DYNAMICIC_COMPLEXITY = new Metric.Builder("dynamic_complexity", "Dynamic complexity", Metric.ValueType.INT)
    .setDescription("Number of referenced files")
    .setDirection(Metric.DIRECTION_NONE)
    .setQualitative(false)
    .setDomain(CoreMetrics.DOMAIN_COMPLEXITY)
    .create();

  @Override
  public List<Metric> getMetrics() {
    return Arrays.asList(STATIC_COMPLEXITY, DYNAMICIC_COMPLEXITY);
  }
}
