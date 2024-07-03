ThisBuild / version := "1.0.0"

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

assemblyMergeStrategy in assembly := {
  case PathList("module-info.class") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

assemblyJarName in assembly := s"Scala2Prolog-${version.value}.jar"
