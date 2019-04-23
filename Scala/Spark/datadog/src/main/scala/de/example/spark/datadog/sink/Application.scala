// Author: Gustavo Martin Morcuende
package de.example.spark.datadog.sink

import com.typesafe.scalalogging.{LazyLogging}

object Application extends App with LazyLogging {

  for (season <- List("fall", "winter", "spring"))
    logger.info(season + ":" + season)
}
