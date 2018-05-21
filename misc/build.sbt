name := "Misc"
version := "1.0"

val akkaStreamsVersion = "2.5.11"
val scalazVersion = "7.2.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaStreamsVersion,
  //  "com.typesafe.akka" %% "akka-typed-experimental" % akkaStreamsVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaStreamsVersion,
  "org.json4s" %% "json4s-native" % "3.5.0",
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "org.scalaz" %% "scalaz-concurrent" % scalazVersion,
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.8",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaStreamsVersion,
  "com.chuusai" %% "shapeless" % "2.3.2",
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "joda-time" % "joda-time" % "2.9.9",
  "org.xmlunit" % "xmlunit-core" % "2.5.1"
)
