import sbt._

object Dependencies {

  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"
  val `json4s-native` = "org.json4s" %% "json4s-native" % "4.0.6"
  val scalatest = "org.scalatest" %% "scalatest" % "3.2.14"
  val scalameter = "com.storm-enroute" %% "scalameter" % "0.21"
  val logback = "ch.qos.logback" % "logback-classic" % "1.4.5"
  val zio = "dev.zio" %% "zio" % "2.0.5"
  val `zio-stream` = "dev.zio" %% "zio-streams" % "2.0.5"

}
