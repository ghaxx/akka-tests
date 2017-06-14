name := "HTTP Clients"
version := "1.0"

val akkaStreamsVersion = "2.4.16"
val scalazVersion = "7.2.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.5" ,
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.5" ,
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.8",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.chuusai" %% "shapeless" % "2.3.2",
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "io.shaka" %% "naive-http" % "90",
  "org.asynchttpclient" % "async-http-client" % "2.0.29",
  "org.apache.httpcomponents" % "httpasyncclient" % "4.1.3",
  "commons-io" % "commons-io" % "2.5",
  "com.squareup.okhttp3" % "okhttp" % "3.6.0"
)

resolvers += "Tim Tennant's repo" at "http://dl.bintray.com/timt/repo/"
