// Author: Gustavo Martin Morcuende
package de.example.playground.spark.kafka

import de.example.playground.commons.test.spark.SharedSparkSessionHelper

class SparkKafkaIntegrationTest extends SharedSparkSessionHelper {

  it should "create table Kafka with succes" in {
    val dbName = "gustavo"
    val tableName = "kafkaexample"
    val createDatabaseStatement = s"CREATE DATABASE IF NOT EXISTS $dbName"
    val createTableStatement =
      s"CREATE TABLE IF NOT EXISTS $dbName.$tableName " +
        "USING KAFKA " +
        "OPTIONS " +
        "( " +
        "'subscribe' 'priv.track.milanuncios.application-opened', " +
        "'kafka.bootstrap.servers' 'kafka://kafka-greensilence.storage.schibsted.io:9092', " +
        "'key.serializer' 'org.apache.kafka.common.serialization.ByteArraySerializer', " +
        "'value.serializer' 'org.apache.kafka.common.serialization.StringSerializer', " +
        "'key.deserializer' 'org.apache.kafka.common.serialization.StringDeserializer', " +
        "'value.deserializer' 'org.apache.kafka.common.serialization.StringDeserializer' " +
        " ) "

    spark.sql(createDatabaseStatement)
    spark.sql(createTableStatement)

    spark
      .sql(s"SELECT * FROM $dbName.$tableName")
      .selectExpr("CAST(value AS STRING) as sValue")
      .write
      .text(path)
  }
}
