package pl.streams.ordering

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import pl.MySpec
import pl.streams.partitioned.{MapAsyncToWorkers, Msg}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class OrderingTest extends MySpec {

  implicit val system = ActorSystem("test")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer.create(system)

  val messages = List(
    Msg(1, 1, 1000),
    Msg(2, 1, 1000),
    Msg(3, 2, 1000),
    Msg(4, 1, 1000),
    Msg(5, 1, 1000),
    Msg(6, 2, 1000)
  )

  val parallelism = 2

  val flow = Flow[Msg].map { s =>
    s.work
  }

  def groupByStream = Source(messages)
    .groupBy(parallelism, cls ⇒ cls.key(parallelism))
    .via(flow.async)
    .mergeSubstreams

  def mapAsyncStream = Source(messages)
    .via(MapAsyncToWorkers(parallelism, s => Future(s.work), s => s.key(parallelism), 20))

  "Stream" should "keep order of elements?" in {
    val result = groupByStream.runFold(List.empty[Msg]){
      (m, e) => e :: m
    }

    logTime("ordered") {
      val r = Await.result(result, 10 seconds).reverse
      println("""r = """ + r)
    }
  }
  it should "keep order of elements???" in {
    val result = mapAsyncStream.runFold(List.empty[Msg]){
      (m, e) => e :: m
    }

    logTime("map async") {
      val r = Await.result(result, 10 seconds).reverse
      println("""r = """ + r)
    }
  }

}
