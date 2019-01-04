name := "Akka Stream Playground"
version := "1.0"

val akkaVersion = "2.5.11"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "0.11.0.0" ,
  "org.apache.zookeeper" % "zookeeper" % "3.4.9" ,
  "org.apache.curator" % "curator-test" % "4.0.0",
  "commons-io" % "commons-io" % "2.6",
  "net.cakesolutions" %% "scala-kafka-client-testkit" % "0.11.0.1",
)

resolvers += Resolver.bintrayRepo("cakesolutions", "maven")
