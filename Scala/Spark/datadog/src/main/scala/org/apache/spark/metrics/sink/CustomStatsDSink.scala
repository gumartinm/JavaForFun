// Author: Gustavo Martin Morcuende
package org.apache.spark.metrics.sink

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.codahale.metrics.MetricRegistry
import org.apache.spark.SecurityManager
import org.apache.spark.internal.Logging
import org.apache.spark.metrics.MetricsSystem
import CustomStatsDSink._

// Taken from:
// https://github.com/apache/spark/blob/master/core/src/main/scala/org/apache/spark/metrics/sink/StatsdSink.scala
private[spark] object CustomStatsDSink {
  val STATSD_KEY_HOST = "host"
  val STATSD_KEY_PORT = "port"
  val STATSD_KEY_PERIOD = "period"
  val STATSD_KEY_UNIT = "unit"
  val STATSD_KEY_PREFIX = "prefix"

  val STATSD_DEFAULT_HOST = "127.0.0.1"
  val STATSD_DEFAULT_PORT = "8125"
  val STATSD_DEFAULT_PERIOD = "10"
  val STATSD_DEFAULT_UNIT = "SECONDS"
  val STATSD_DEFAULT_PREFIX = "defaultcustomsparkprefix"
}

private[spark] class CustomStatsDSink(val property: Properties,
                                      val registry: MetricRegistry,
                                      securityMgr: SecurityManager)
    extends Sink
    with Logging {

  val host = property.getProperty(STATSD_KEY_HOST, STATSD_DEFAULT_HOST)
  val port = property.getProperty(STATSD_KEY_PORT, STATSD_DEFAULT_PORT).toInt

  val pollPeriod = property.getProperty(STATSD_KEY_PERIOD, STATSD_DEFAULT_PERIOD).toInt
  val pollUnit =
    TimeUnit.valueOf(property.getProperty(STATSD_KEY_UNIT, STATSD_DEFAULT_UNIT).toUpperCase)

  val prefix = property.getProperty(STATSD_KEY_PREFIX, STATSD_DEFAULT_PREFIX)

  MetricsSystem.checkMinimalPollingPeriod(pollUnit, pollPeriod)

  val reporter = new DatadogReporter(registry, host, port, prefix)

  override def start(): Unit = {
    reporter.start(pollPeriod, pollUnit)
    logInfo(s"StatsdSink started with prefix: '$prefix'")
  }

  override def stop(): Unit = {
    reporter.stop()
    logInfo("StatsdSink stopped.")
  }

  override def report(): Unit = reporter.report()
}
