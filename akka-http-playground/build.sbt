name := "akka-http-playground"
version := "1.0"

val akkaStreamsVersion = "2.4.16"
val scalazVersion = "7.2.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.5",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.5",
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
  "com.squareup.okhttp3" % "okhttp" % "3.6.0",
  "commons-io" % "commons-io" % "2.4"
)

resolvers += "Tim Tennant's repo" at "http://dl.bintray.com/timt/repo/"

enablePlugins(DockerPlugin)
dockerfile in docker := {
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  new Dockerfile {
    from("java")
    add(artifact, artifactTargetPath)
    entryPoint("java", "-jar", artifactTargetPath)
  }
}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "io.netty.versions.properties", xs @ _*) => MergeStrategy.last
  case x =>
    val old = (assemblyMergeStrategy in assembly).value
    old(x)
}

imageNames in docker := Seq(
  ImageName(s"${organization.value}/${name.value}:latest"),
  ImageName(
    namespace = Some(organization.value),
    repository = name.value,
    tag = Some("v" + version.value)
  )
)
