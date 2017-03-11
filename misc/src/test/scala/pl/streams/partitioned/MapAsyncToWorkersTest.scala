package pl.streams.partitioned

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import pl.MySpec

import scala.concurrent.Future

class MapAsyncToWorkersTest extends MySpec with ParallelBehavior {

  implicit val system = ActorSystem("test")
  implicit val executionContext = system.dispatcher
  //  implicit val executionContext = system.dispatchers.lookup("my-dispatcher")
  implicit val materializer = ActorMaterializer.create(system)

  val messages = List(Msg(1, 1), Msg(2, 2), Msg(3, 3), Msg(4, 4))

  val parallelism = 50

  def customStream = Source(messages)
    .via(MapAsyncToWorkers(parallelism, s => Future(s.work), s => s.key(parallelism), 20))

  "Component" should behave like parallelStream {
    MapAsyncToWorkers(10, s => Future.successful(s), identity, 10)
  }

  it should behave like parallelStreamHandlingErrors {
    MapAsyncToWorkers(10, s => Future {
      if (s == Msg(1)) throw new IllegalArgumentException
      else s
    }, s => s)
  }

}
