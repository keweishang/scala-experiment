lazy val sprayJsonVersion = "1.3.3"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "io.keweishang",
      scalaVersion := "2.12.2",
      version := "0.1.0-SNAPSHOT"
    )),
    name := "scala-experiment",
    libraryDependencies ++= Seq(
      "io.spray" %% "spray-json" % sprayJsonVersion,
      "org.scalatest" %% "scalatest" % "3.0.1" % Test
    )
  )
