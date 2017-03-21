name := "Slick Playground"
version := "1.0"

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.4.194",
  "com.typesafe.slick" %% "slick" % "3.2.0",
  "com.typesafe" % "config" % "1.2.1",
  "joda-time" % "joda-time" % "2.9.7"
)