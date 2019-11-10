// Author: Gustavo Martin Morcuende
package de.example.playground.commons.test.spark

import java.io.File
import java.nio.file.Files

import org.apache.spark.sql.{SQLContext, SQLImplicits, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
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
    .enableHiveSupport()
    .getOrCreate()

  protected var path: String = _
  protected implicit def spark: SparkSession = _spark

  protected def sparkContext: SparkContext = _spark.sparkContext

  protected def sparkConf: SparkConf = {
    new SparkConf()
      .set("spark.unsafe.exceptionOnMemoryLeak", "true")
      .set("spark.ui.enabled", "false")
      .set("spark.sql.sources.partitionOverwriteMode", "dynamic")
      .set("hive.stats.jdbc.timeout", "80")
      //.set("spark.hadoop.hive.metastore.uris", "thrift://localhost:9083") // Comment this line out for using embedded
      //.set("spark.sql.warehouse.dir", "/apps/hive/warehouse")             // Comment this line out for using embedded
  }

  protected override def beforeEach(): Unit = {
    super.beforeEach()
    path = Files.createTempDirectory(this.getClass.getName).toString
  }

  protected override def afterEach(): Unit = {
    super.afterEach()
    new Directory(new File(path)).deleteRecursively()

    spark.sharedState.cacheManager.clearCache()
    spark.sessionState.catalog.reset()
  }

  protected object testImplicits extends SQLImplicits {
    // scalastyle:off method.name
    protected override def _sqlContext: SQLContext = _spark.sqlContext
    // scalastyle:on method.name
  }
}
