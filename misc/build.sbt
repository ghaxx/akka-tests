name := "Misc"
version := "1.0"

val akkaStreamsVersion = "2.7.0"
val scalazVersion = "7.3.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaStreamsVersion,
  //  "com.typesafe.akka" %% "akka-typed-experimental" % akkaStreamsVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaStreamsVersion,
  "org.json4s" %% "json4s-native" % "4.0.6",
  "org.json4s" %% "json4s-jackson" % "4.0.6",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.1",
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "org.scalatest" %% "scalatest" % "3.2.14" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.8",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaStreamsVersion,
  "com.chuusai" %% "shapeless" % "2.3.9",
  "org.scalaj" %% "scalaj-http" % "2.4.2"
)
