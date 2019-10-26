// Author: Gustavo Martin Morcuende
package de.example.sbt

import java.io.File
import java.nio.file.Files

import org.apache.spark.SparkConf
import org.apache.spark.sql.{SQLContext, SQLImplicits, SparkSession}
import org.scalatest._
import org.scalatest.prop.{Checkers, PropertyChecks}

import scala.reflect.io.Directory

trait SharedSparkSessionHelper
    extends FlatSpec
    with Matchers
    with OptionValues
    with Inside
    with Inspectors
    with GivenWhenThen
    with Checkers
    with PropertyChecks
    with BeforeAndAfterEach {

  private val _spark = SparkSession
    .builder()
    .master("local[2]")
    .appName("test-sql-context")
    .config(sparkConf)
    .config("spark.unsafe.exceptionOnMemoryLeak", "true")
    .config("spark.ui.enabled", "false")
    .config("spark.sql.sources.partitionOverwriteMode", "dynamic")
    .config("hive.stats.jdbc.timeout", "80")
    .config("spark.hadoop.hive.metastore.uris", "thrift://localhost:9083")
    .config("spark.sql.warehouse.dir", "/apps/hive/warehouse")
    .enableHiveSupport()
    .getOrCreate()

  protected var path: String = _
  protected implicit def spark: SparkSession = _spark

  protected def sparkContext = _spark.sparkContext

  protected def sparkConf = {
    new SparkConf()
      .set("spark.unsafe.exceptionOnMemoryLeak", "true")
      .set("spark.ui.enabled", "false")
      .set("spark.sql.sources.partitionOverwriteMode", "dynamic")
      .set("hive.stats.jdbc.timeout", "80")
      .set("spark.hadoop.hive.metastore.uris", "thrift://localhost:9083")
      .set("spark.sql.warehouse.dir", "/apps/hive/warehouse")

  }

  protected override def beforeEach(): Unit = {
    path = Files.createTempDirectory(this.getClass.getName).toString
  }

  protected override def afterEach(): Unit = {
    new Directory(new File(path)).deleteRecursively()

    spark.sharedState.cacheManager.clearCache()
  }

  protected object testImplicits extends SQLImplicits {
    protected override def _sqlContext: SQLContext = _spark.sqlContext
  }
}
