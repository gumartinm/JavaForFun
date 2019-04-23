// Author: Gustavo Martin Morcuende
package de.example.spark.datadog.sink.domain.repository.meter

trait MeterRepository {

  def count(metricName: String, tags: String*)

}
