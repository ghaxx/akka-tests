package pl

import net.logstash.logback.marker.LogstashMarker
import org.slf4j.{LoggerFactory, Marker, MarkerFactory}

import scala.concurrent.{Await, Future}
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object MultiLoggingPlaygroundApp extends App {

  import net.logstash.logback.argument.StructuredArguments._
  import net.logstash.logback.marker.Markers._

//  val logger = LoggerFactory.getLogger(getClass)
  private val logger = LoggerFactory.getLogger("pl.task")

  //  logger.info("Log normal with a marker")
  //  logger.info(MarkerFactory.getMarker("A Marker 1"), "Log with a marker")
  //  logger.info("Log with a keyValue", keyValue("key1", "value 1"))
  //  logger.info(MarkerFactory.getMarker("A Marker 2"), "Log with a marker and a keyValue", keyValue("key2", "value 2"))
  //  logger.info(MarkerFactory.getMarker("A Marker 3"), "Log with a marker and 2 keyValue", keyValue("key3.1", "value 3.1"), keyValue("key3.2", "value 3.2"): Any)
  private val markers: Marker = append("app_type", "multi")


  logger.info(markers, "Starting")

  (1 to 1000) foreach { _ =>
    val tasks = (1 to Random.nextInt(10) + 2) map {i =>
      new Task(i).run()
    }

    Await.result(Future.sequence(tasks), 5 seconds)
    Thread.sleep(3000 + Random.nextInt(5000))
  }


class Task(id: Int) {
  import net.logstash.logback.marker.Markers._
//  private val logger = LoggerFactory.getLogger("pl.task")
  def run(): Future[Unit] = Future {
    var totalTime = 0
    val tasksNumber = Random.nextInt(40) + 2
    (1 to tasksNumber).foreach { i =>
      val generatedTaskTime = Random.nextInt(3)
      totalTime += generatedTaskTime
      val markers = append("single_time", generatedTaskTime)
      logger.info(markers, s"$id: Running task $i of $tasksNumber: time $generatedTaskTime ${"mmm " * 20}")
    }
    val markers = append("total_time", totalTime)
    logger.info(markers, s"$id: Total time $totalTime")
  }
}

}