// Author: Gustavo Martin Morcuende
package de.example.spark.datadog.sink.infrastructure.configuration
import com.codahale.metrics.MetricRegistry

object RepositoryConfiguration {

  def metricRegistry(): MetricRegistry = {
      new MetricRegistry()
  }


}
