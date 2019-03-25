// Author: Gustavo Martin Morcuende
package org.apache.spark.metrics.source
import com.codahale.metrics.MetricRegistry

private[spark] class CustomSource extends Source {

  override def sourceName: String = "custom"
  override def metricRegistry: MetricRegistry = new MetricRegistry()
}
