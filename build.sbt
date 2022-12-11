//import sbtassembly.Plugin._
//import AssemblyKeys._
scalaVersion  := "2.13.10"
ThisProject / scalaVersion  := "2.13.10"

lazy val `scala-tests` = project.in(file("."))
  .settings(
    name := "Scala Tests",
    version := "1.0",
    //    assemblyShadeRules in assembly := Seq(
    ////      ShadeRule.zap("ch.**", "scala.**", "com.**").inAll,
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

lazy val `akka-actors-playground` = project.settings(CommonSettings)
lazy val `akka-http-playground` = project.settings(CommonSettings).dependsOn(`performance-test-kit`)
lazy val `streams-playground` = project.settings(CommonSettings).dependsOn(`performance-test-kit`)
lazy val `akka-streams-playground` = project.settings(CommonSettings).dependsOn(`performance-test-kit`)
lazy val `akka-streams-kafka-playground` = project.settings(CommonSettings).dependsOn(`performance-test-kit`)
lazy val `performance-test-kit` = project.settings(CommonSettings)
lazy val misc = project.settings(CommonSettings)
lazy val `slick-playground` = project.settings(CommonSettings).dependsOn(`performance-test-kit`)
//lazy val `scala-async-http-client` = RootProject(uri("git://github.com/niedzwiedzislaw/scala-async-http-client.git"))

lazy val CommonLibraryDependencies = Seq(
  Dependencies.scalatest % "test",
  Dependencies.logback,
  "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.5",
  Dependencies.`json4s-native`,
  Dependencies.scalameter,
  "ndz" %% "scala-async-http-client" % "1.0",
  Dependencies.`zio-stream`
)

lazy val CommonSettings = Seq(
  scalaVersion  := "2.13.10",
  libraryDependencies ++= CommonLibraryDependencies
)
