ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % "test"
libraryDependencies += "it.unibo.alice.tuprolog" % "2p-core" % "4.1.1"
libraryDependencies += "it.unibo.alice.tuprolog" % "2p-ui" % "4.1.1"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.23.1"
libraryDependencies += "org.apache.logging.log4j" %% "log4j-api-scala" % "13.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "exam",
    idePackagePrefix := Some("pps.exam.application")
  )
