// Author: Gustavo Martin Morcuende
package de.example.playground.spark.hive

import de.example.playground.commons.test.spark.SharedSparkSessionHelper

class ExampleIntegrationTest extends SharedSparkSessionHelper {

  it should "create table Hive with succes" in {
    val dbName = "gustavo"
    val tableName = "example"
    val schema = "gustavo string, years bigint"
    val createDatabaseStatement = s"CREATE DATABASE IF NOT EXISTS $dbName"
    val createTableStatement =
      s"CREATE TABLE IF NOT EXISTS $dbName.$tableName " +
        s"($schema) " +
        "USING HIVE " +
        "OPTIONS " +
        "( " +
        "'serde' 'org.apache.hive.hcatalog.data.JsonSerDe', " +
        s"'path' '$path' " +
        " ) "

    spark.sql(createDatabaseStatement)
    spark.sql(createTableStatement)

    val tableExists: Boolean = spark.catalog.tableExists(dbName, tableName)
    tableExists shouldBe true
  }
}
