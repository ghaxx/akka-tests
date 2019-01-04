//import sbtassembly.Plugin._
//import AssemblyKeys._

scalaVersion in ThisBuild := "2.12.4"
organization in ThisBuild := "ghx"

lazy val `scala-tests` = project.in(file("."))
  .settings(
    name := "Scala Tests",
    version := "1.0"
    //    assemblyShadeRules in assembly := Seq(
    //      ShadeRule.zap("ch.**", "scala.**", "com.**").inAll,
    //      ShadeRule.rename("org.json4s.**" -> "rm.org.json4s.@1").inAll,
    //      ShadeRule.keep("**").inProject,
    //      ShadeRule.keep("pl.**", "rm.**", "org.json4s.**").inAll
    //    )
  )

//artifact in(Compile, assembly) := {
//  val art = (artifact in(Compile, assembly)).value
//  art.copy(`classifier` = Some("assembly"))
//}

//addArtifact(artifact in(Compile, assembly), assembly)

lazy val `akka-stream-playground` = project.dependsOn(`test-kit`)
lazy val `akka-actors-playground` = project.dependsOn(`test-kit`)
lazy val `http-clients` = project.dependsOn(`test-kit`, `scala-async-http-client`)
lazy val `akka-http-playground` = project.dependsOn(`scala-async-http-client`)
lazy val `static-resources-server` = project
lazy val `logstash-playground` = project
lazy val `spray-playground` = project
lazy val `cache-tests` = project.dependsOn(`test-kit`)
lazy val `test-kit` = project
lazy val misc = project.dependsOn(`test-kit`)
lazy val `slick-playground` = project.dependsOn(`test-kit`)
lazy val `scala-async-http-client` = RootProject(uri("https://github.com/ghaxx/scala-async-http-client.git"))
lazy val `spray-client` = project
lazy val `local-kafka` = project

//libraryDependencies in ThisBuild ++= Seq(
//  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
//  "ch.qos.logback" % "logback-classic" % "1.2.3",
//  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
//  "org.json4s" %% "json4s-native" % "3.5.0"
//)

scalacOptions in ThisBuild ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)
