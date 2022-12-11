version := "1.0"
scalaVersion  := "2.13.10"

val akkaStreamsVersion = "2.7.0"
val scalazVersion = "7.3.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka"          %% "akka-http"                 % "10.4.0",
  "org.scalaz"                 %% "scalaz-core"               % scalazVersion,
  "org.scalatest"              %% "scalatest"                 % "3.2.14"        % "test",
  "ch.qos.logback"             %  "logback-classic"           % "1.4.5",
  "com.typesafe.scala-logging" %% "scala-logging"             % "3.9.5",
  "com.chuusai"                %% "shapeless"                 % "2.3.9",
  "org.scalaj"                 %% "scalaj-http"               % "2.4.2",
  "io.shaka"                   %% "naive-http"                % "124",
  "org.asynchttpclient"        %  "async-http-client"         % "2.0.29",
  "org.apache.httpcomponents"  %  "httpasyncclient"           % "4.1.3",
  "commons-io"                 %  "commons-io"                % "2.5",
  "com.squareup.okhttp3"       %  "okhttp"                    % "3.6.0",
  "io.gatling.highcharts"      %  "gatling-charts-highcharts" % "3.8.4"        % "test",
  "io.gatling"                 %  "gatling-test-framework"    % "3.8.4"        % "test"
)

//resolvers += "Tim Tennant's repo" at "http://dl.bintray.com/timt/repo/"
