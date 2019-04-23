// Author: Gustavo Martin Morcuende
package de.example.spark.datadog.sink.infrastructure.meter
import de.example.spark.datadog.sink.domain.repository.meter.MeterRepository

class MeterRepositoryCodahale extends MeterRepository {

  override def count(
      metricName: String,
      tags: String*): Unit = ???
}
