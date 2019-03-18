// Author: Gustavo Martin Morcuende
package de.example.spark.datadog.sink

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object Application extends App {
  val logger: Logger = Logger(LoggerFactory.getLogger(getClass.getName))

  for (season <- List("fall", "winter", "spring"))
    logger.info(season + ":" + season)
}
