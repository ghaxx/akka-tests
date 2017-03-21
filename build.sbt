scalaVersion in ThisBuild := "2.12.1"

lazy val `scala-tests` = project.in(file("."))
  .settings(
    name := "Scala Tests",
    version := "1.0"
  )

lazy val `akka-actors-playground` = project
lazy val `akka-http-playground` = project.dependsOn(`performance-kit`, `scala-async-http-client`)
lazy val `performance-kit` = project
lazy val misc = project
lazy val `slick-playground` = project.dependsOn(`performance-kit`)
lazy val `scala-async-http-client` = RootProject(uri("git://github.com/ghaxx/scala-async-http-client.git"))
lazy val `spray-client` = project

libraryDependencies in ThisBuild ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.8",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
)

