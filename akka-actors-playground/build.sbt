name := "Akka Actors Playground"
version := "1.0"

val akkaVersion = "2.7.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  //  "com.typesafe.akka" %% "akka-typed-experimental" % akkaStreamsVersion,
)

// `zio-stream`nt's repo" at "http://dl.bintray.com/timt/repo/"
