name := "GitHub Client"
version := "1.0"
scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.8",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "org.asynchttpclient" % "async-http-client" % "2.0.29",
  "org.json4s" %% "json4s-native" % "3.5.0"
)