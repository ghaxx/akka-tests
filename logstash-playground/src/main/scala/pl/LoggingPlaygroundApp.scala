package pl

import net.logstash.logback.marker.LogstashMarker
import org.slf4j.{LoggerFactory, Marker, MarkerFactory}

object LoggingPlaygroundApp extends App {

  import net.logstash.logback.argument.StructuredArguments._
  import net.logstash.logback.marker.Markers._

  val logger = LoggerFactory.getLogger(getClass)

  //  logger.info("Log normal with a marker")
  //  logger.info(MarkerFactory.getMarker("A Marker 1"), "Log with a marker")
  //  logger.info("Log with a keyValue", keyValue("key1", "value 1"))
  //  logger.info(MarkerFactory.getMarker("A Marker 2"), "Log with a marker and a keyValue", keyValue("key2", "value 2"))
  //  logger.info(MarkerFactory.getMarker("A Marker 3"), "Log with a marker and 2 keyValue", keyValue("key3.1", "value 3.1"), keyValue("key3.2", "value 3.2"): Any)
  private val markers: Marker =
  append("Mark 1", "Val  1")
    .and(append("Mark 2", "Val 2")).asInstanceOf[LogstashMarker]
    .and(MarkerFactory.getMarker("A tag 2"))


  logger.info(markers, "333 Log with a marker and 2 keyValue new", keyValue("key3.1", "value 3.1"), keyValue("key3.2", "value 3.2"): Any)

}