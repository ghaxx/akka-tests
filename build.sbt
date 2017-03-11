scalaVersion in ThisBuild := "2.12.1"

lazy val `scala-tests` = project.in(file("."))
  .settings(
    name := "Scala Tests",
    version := "1.0"
  )

lazy val `akka-http-playground` = project.dependsOn(`performance-kit`, `scala-async-http-client`)
lazy val `spray-client` = project
lazy val `performance-kit` = project
lazy val `github-client` = project.dependsOn(`scala-async-http-client`)
lazy val `scala-async-http-client` = project
lazy val misc = project
lazy val `akka-actors-playground` = project

resolvers += "Akka Snapshot Repository" at "pl.http://repo.akka.io/snapshots/"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)
resolvers += "spray repo" at "pl.http://repo.spray.io"
