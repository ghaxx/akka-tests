import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object Main extends App {

  implicit val system = ActorSystem("test")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer.create(system)

  private val input: List[Int] = List(1, 1, 2, 3, 1, 1)
  val outputF = Source(input)
      .groupBy(3, identity)
    .map {
      x =>
        println(x)
        Thread.sleep(x * 1000)
        x
    }
//    .async
    .mergeSubstreams
    .runFold(List.empty[Int]) {
      (m, e) =>
        println("m = " + m)
        e :: m
    }

  val output = Await.result(outputF, 10 seconds)
  println("output = " + output)
  if (output.length != input.length) throw new RuntimeException("Uneven")
}