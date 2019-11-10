ThisBuild / scalaVersion := "2.11.8"
ThisBuild / organization := "de.example.playground"
ThisBuild / version := "0.1.0-SNAPSHOT"
name := "playground"

val playGroundCommonsProjectName = "playground-commons"
lazy val playgroundCommonsProject = (project in file(playGroundCommonsProjectName))
  .withId(playGroundCommonsProjectName)
  .settings(
    name := playGroundCommonsProjectName,
    settings,
    libraryDependencies ++= commonDependencies,
    organization := "de.example.playground.commons",
    publishArtifact in Test := true
  )

val playGroundSparkHiveProjectName = "playground-spark-hive"
lazy val playGroundSparkHiveProject = (project in file(playGroundSparkHiveProjectName))
  .withId(playGroundSparkHiveProjectName)
  .settings(
    name := playGroundSparkHiveProjectName,
    settings,
    libraryDependencies ++= commonDependencies,
    organization := "de.example.playground.spark.hive"
  )
  .dependsOn(
    playgroundCommonsProject
  )


lazy val dependencies =
  new {
    val sparkVersion = "2.4.0"
    // Hortonworks Atlas Connector
    // val sparkVersion = "2.3.2"

    // Logging
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"

    // Spark
    val sparkSql = "org.apache.spark" %% "spark-sql" % sparkVersion
    val sparkCore = "org.apache.spark" %% "spark-core" % sparkVersion
    val sparkHive = "org.apache.spark" %% "spark-hive" % sparkVersion
    val jacksonCore = "com.fasterxml.jackson.core" % "jackson-core" % "2.6.7"
    val jacksonDataBind = "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7"
    val jacksonAnnotations = "com.fasterxml.jackson.core" % "jackson-annotations" % "2.6.7"

    // Hive
    val hiveCatalogCore = "org.apache.hive.hcatalog" % "hive-hcatalog-core" % "1.2.1"

    // Test
    val scalatest = "org.scalatest" %% "scalatest" % "3.0.5" % Test
    val mockitoScala = "org.mockito" %% "mockito-scala" % "1.0.4" % Test
    val junit = "junit" % "junit" % "4.12" % Test
    val scalacheck = "org.scalacheck" %% "scalacheck" % "1.14.2" % Test
  }

lazy val commonDependencies = Seq(
  dependencies.scalaLogging,
  dependencies.sparkSql,
  dependencies.sparkCore,
  dependencies.sparkHive,
  dependencies.jacksonCore,
  dependencies.jacksonDataBind,
  dependencies.hiveCatalogCore,
  dependencies.scalatest,
  dependencies.mockitoScala,
  dependencies.junit,
  dependencies.scalacheck
)

lazy val settings = scalaStyleSettings

lazy val scalaStyleSettings =
  Seq(
    scalastyleFailOnError := true,
    scalastyleFailOnWarning := true,
    (scalastyleFailOnError in Test) := true,
    (scalastyleFailOnWarning in Test) := true
  )
