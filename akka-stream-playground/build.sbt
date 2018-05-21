name := "Akka Stream Playground"
version := "1.0"

val akkaVersion = "2.5.11"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.16",
  "com.lightbend.akka" %% "akka-stream-alpakka-jms" % "0.11",
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "javax.jms" % "jms" % "1.1",

  "org.apache.kafka" %% "kafka" % "0.11.0.0" exclude("org.slf4j", "slf4j-log4j12"),
  "org.apache.zookeeper" % "zookeeper" % "3.4.9" exclude("org.slf4j", "slf4j-log4j12"),
  "org.apache.curator" % "curator-test" % "4.0.0",
//  "net.cakesolutions" %% "scala-kafka-client-testkit" % "0.11.0.0",

  "org.apache.activemq" % "activemq-core" % "5.7.0", // -all has slf4j shaded inside
  "org.apache.activemq" % "activemq-pool" % "5.15.3"
//  "org.apache.activemq" % "activemq-core" % "5.15.0"
)

resolvers += Resolver.bintrayRepo("cakesolutions", "maven")
