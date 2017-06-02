name := "Akka Actors Playground"
version := "1.0"

val akkaStreamsVersion = "2.4.16"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaStreamsVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaStreamsVersion
  //  "com.typesafe.akka" %% "akka-typed-experimental" % akkaStreamsVersion,
)

resolvers += "Tim Tennant's repo" at "http://dl.bintray.com/timt/repo/"
