name := "Spray Playground"
version := "1.0"
scalaVersion := "2.11.9"

val akkaStreamsVersion = "2.4.16"
val scalazVersion = "7.2.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.15",
  "io.spray" %% "spray-can" % "1.3.3",
  "io.spray" %% "spray-routing" % "1.3.3",
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.8",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "commons-io" % "commons-io" % "2.5",
  "com.squareup.okhttp3" % "okhttp" % "3.6.0"
)

// `zio-stream`nt's repo" at "http://dl.bintray.com/timt/repo/"
