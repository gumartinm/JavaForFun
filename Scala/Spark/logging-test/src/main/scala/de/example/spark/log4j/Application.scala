// Author: Gustavo Martin Morcuende
package de.example.spark.log4j

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

object Application extends App with LazyLogging {
  private val JvmSource = "org.apache.spark.metrics.source.JvmSource"
  private val Limit = 10

  private val source = args(0)
  private val baseConfig = new SparkConf(loadDefaults = true)
  private val builder: SparkSession.Builder = SparkSession
    .builder()
    .config(baseConfig)
    .config("spark.metrics.conf.*.sink.customstatsd.class", "org.apache.spark.metrics.sink.CustomStatsDSink")
    .config("spark.metrics.conf.*.sink.customstatsd.prefix", "gussparkprefix")
    .config("spark.metrics.conf.master.source.jvm.class", JvmSource)
    .config("spark.metrics.conf.worker.source.jvm.class", JvmSource)
    .config("spark.metrics.conf.driver.source.jvm.class", JvmSource)
    .config("spark.metrics.conf.executor.source.jvm.class", JvmSource)
    .appName("Logging-Test")

  private val sparkSession = builder.getOrCreate()

  private val dataFrame: DataFrame = sparkSession.read.parquet(source)

  private val message = "HELLO GUS"
  logger.info(message)
  logger.error(message)
  logger.warn(message)

  for (row <- dataFrame.head(Limit)) {

    val row_info = row.mkString(",")
    logger.info(row_info)
    logger.error(row_info)
    logger.warn(row_info)
  }
}
