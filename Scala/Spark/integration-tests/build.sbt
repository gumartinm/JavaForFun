ThisBuild / scalaVersion := "2.11.8"
ThisBuild / organization := "de.example.sbt"
ThisBuild / version := "0.1.0-SNAPSHOT"
name := "integration-tests"

val sparkVersion = "2.3.2"

// Logging
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"

// Hive
libraryDependencies += "org.apache.hive.hcatalog" % "hive-hcatalog-core" % "1.2.1"

// Spark
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-hive" % sparkVersion

// Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
libraryDependencies += "org.mockito" %% "mockito-scala" % "1.0.4" % Test
libraryDependencies += "junit" % "junit" % "4.12" % Test
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.2" % Test



scalastyleFailOnError := true
scalastyleFailOnWarning := true
(scalastyleFailOnError in Test) := true
(scalastyleFailOnWarning in Test) := true
