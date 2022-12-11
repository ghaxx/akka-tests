name := "Slick Playground"
version := "1.0"

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "2.1.214",
  "com.typesafe.slick" %% "slick" % "3.4.1",
  "com.typesafe" % "config" % "1.4.2",
  "joda-time" % "joda-time" % "2.9.7"
)
