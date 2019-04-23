// Author: Gustavo Martin Morcuende
package org.apache.spark.metrics.sink

import java.util
import java.util.EnumSet
import java.util.Properties
import java.util.concurrent.TimeUnit

import com.codahale.metrics.MetricRegistry
import com.typesafe.scalalogging.Logger
import org.apache.spark.SecurityManager
import org.coursera.metrics.datadog.DatadogReporter
import org.coursera.metrics.datadog.DatadogReporter.Expansion.COUNT
import org.coursera.metrics.datadog.transport.UdpTransport
import org.slf4j.LoggerFactory

// Taken from:
// https://github.com/apache/spark/blob/master/core/src/main/scala/org/apache/spark/metrics/sink/StatsdSink.scala
private[spark] object CustomStatsDSink {
  val STATSD_KEY_HOST = "host"
  val STATSD_KEY_PORT = "port"
  val STATSD_KEY_PERIOD = "period"
  val STATSD_KEY_UNIT = "unit"
  val STATSD_KEY_PREFIX = "prefix"

  val STATSD_DEFAULT_HOST = "127.0.0.1"
  val STATSD_DEFAULT_PORT = 8125
  val STATSD_DEFAULT_PERIOD = "10"
  val STATSD_DEFAULT_UNIT = "SECONDS"
  val STATSD_DEFAULT_PREFIX = "defaultcustomsparkprefix"
}

private[spark] class CustomStatsDSink(val property: Properties,
                       val registry: MetricRegistry,
                       securityMgr: SecurityManager) extends Sink {
  private val logger: Logger = Logger(LoggerFactory.getLogger(getClass.getName))

  private val prefix = property.getProperty(CustomStatsDSink.STATSD_KEY_PREFIX,
                                            CustomStatsDSink.STATSD_DEFAULT_PREFIX)
  private val pollPeriod = property.getProperty(CustomStatsDSink.STATSD_KEY_PERIOD,
                                                CustomStatsDSink.STATSD_DEFAULT_PERIOD).toInt
  private val pollUnit = TimeUnit.valueOf(property.getProperty(CustomStatsDSink.STATSD_KEY_UNIT,
                                                               CustomStatsDSink.STATSD_DEFAULT_UNIT).toUpperCase)

  private val udpTransport = new UdpTransport.Builder()
                                .withPort(CustomStatsDSink.STATSD_DEFAULT_PORT)
                                .withStatsdHost(CustomStatsDSink.STATSD_DEFAULT_HOST)
                                .build()
  private val expansions = util.EnumSet.of(COUNT)
  private val reporter = DatadogReporter
                            .forRegistry(registry)
                            .withHost("customhost")
                            .withTransport(udpTransport)
                            .withExpansions(expansions)
                            .withPrefix(prefix)
                            .build()

  override def start(): Unit = {
    reporter.start(pollPeriod, pollUnit)
    logger.info(s"CustomStatsDSink started with prefix: '$prefix'")

  }
  override def stop(): Unit = {
    reporter.stop()
    logger.info("CustomStatsDSink stopped.")
  }
  override def report(): Unit = reporter.report()
}
