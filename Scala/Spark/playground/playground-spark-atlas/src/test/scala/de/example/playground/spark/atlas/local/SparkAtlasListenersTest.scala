// Author: Gustavo Martin Morcuende
package de.example.playground.spark.atlas.local

import de.example.playground.commons.test.spark.SharedSparkSessionHelper
import de.example.playground.spark.atlas.local.SparkAtlasListenersTest._
import org.apache.spark.SparkConf
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SaveMode}

private object SparkAtlasListenersTest {
  val PartitionColumnFirstValue = "first"
  val PartitionColumnSecondValue = "second"
  val NameColumnBerenValue = "Beren"
  val NameColumnLuthienValue = "Lúthien"
  val ConditionExprFirstDataFrame = s"df = '$PartitionColumnFirstValue'"
  val conditionExprSecondDataFrame = s"df = '$PartitionColumnSecondValue'"
  val DbName = "db_name"
  val TableName = "table_name"
  val PartitionColumnName = "df"
  val OriginPartitionColumnName = "df_origin"
  val SurNameColumnName = "surname"
  val NameColumnName = "name"
  val FatherColumnName = "father"
  val CreateDatabaseSqlStatement = s"CREATE DATABASE IF NOT EXISTS $DbName"
}

class SparkAtlasListenersTest extends SharedSparkSessionHelper {

  protected override def sparkConf: SparkConf = {
    val conf = super.sparkConf
    conf
      .set("spark.extraListeners", "com.hortonworks.spark.atlas.SparkAtlasEventTracker")
      .set("spark.sql.queryExecutionListeners", "com.hortonworks.spark.atlas.SparkAtlasEventTracker")
  }

  it should "create table Hive with Spark" in {
    val dbName = "gustavo"
    val tableName = "example"
    val schema = "gustavo string, years bigint"
    val createDatabaseStatement = s"CREATE DATABASE IF NOT EXISTS $dbName"
    val createTableStatement =
      s"CREATE TABLE IF NOT EXISTS $dbName.$tableName " +
        s"($schema) " +
        "USING JSON " +
        "LOCATION 'file:/home/gustavo/git/GITHOME/JavaForFun2/Scala/Spark/integration-tests/testOutput' " +
        "PARTITIONED BY (years) "


    spark.sql(createDatabaseStatement)
    spark.sql(createTableStatement)

  }

  ignore should "create table Hive with succes" in {
    val dbName = "gustavo"
    val tableName = "example"
    val schema = "gustavo string, years bigint"
    val createDatabaseStatement = s"CREATE DATABASE IF NOT EXISTS $dbName"
    val createTableStatement =
      s"CREATE TABLE IF NOT EXISTS $dbName.$tableName " +
        s"($schema) " +
        s"USING HIVE " +
        s"OPTIONS " +
        s"( " +
        s"'serde' 'org.apache.hive.hcatalog.data.JsonSerDe', " +
        s"'path' 'file:/home/gustavo/git/GITHOME/JavaForFun2/Scala/Spark/integration-tests/testOutput' " +
        s" ) "

    spark.sql(createDatabaseStatement)
    spark.sql(createTableStatement)

  }

  ignore should "create table with saveAsTable with success" in {
    spark.sql(CreateDatabaseSqlStatement)
    val secondDataFrame = createSecondDataFrameAsColumn
    saveAsTable(secondDataFrame, DbName, TableName, path, PartitionColumnName)

  }

  private def saveAsTable(dataFrame: DataFrame,
                          dbName: String,
                          tableName: String,
                          path: String,
                          partitions: String*): Unit = {
    dataFrame.write
      .mode(SaveMode.Overwrite)
      .option("path", path)
      .format(source = "parquet")
      .partitionBy(partitions: _*)
      .saveAsTable(tableName = s"$dbName.$tableName")
  }

  private def createSecondDataFrameAsColumn: DataFrame = {

    val dataFrame = spark.createDataFrame(
      sparkContext.parallelize(
        Seq(
          Row(NameColumnLuthienValue, "Tinúviel", "Elu Thingol", PartitionColumnSecondValue)
        )),
      StructType(
        List(
          StructField(NameColumnName, StringType, nullable = true),
          StructField(SurNameColumnName, StringType, nullable = true),
          StructField(FatherColumnName, StringType, nullable = true),
          StructField(OriginPartitionColumnName, StringType, nullable = true)
        )
      )
    )
    dataFrame.withColumn(PartitionColumnName, col(OriginPartitionColumnName))
  }
}
