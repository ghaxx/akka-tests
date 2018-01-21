package pl.tracing

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.stream.scaladsl.{MergeHub, Sink, Source}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, OverflowStrategy}
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, Future}

object SourceQueueMHTest extends App {

  import scala.concurrent.duration._

  implicit val system = ActorSystem("main-system")
  implicit val timeout = Timeout(3 seconds)
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(16))
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system).withInputBuffer(initialSize = 1, maxSize = 1))

  val st = System.currentTimeMillis()
  def d = f"${(System.currentTimeMillis() - st) % 100000}%05d"


  val m = MergeHub.source[Int](1)
    .map {
      e =>
        println(s"[$d]Processing: $e")
        Thread.sleep(1000)
        Future successful (e)
    }
    .map {
      e =>
        println(s"[$d]Processed: $e")
        e

    }
    .to(Sink.foreach {
      e =>
        println(s"[$d]Consumed: $e")
    }).run()
  val s = Source.queue[Int](5, OverflowStrategy.dropNew).to(m).run()


  (1 to 100) foreach {
    i =>
      println(s"[$d]Offering $i")
      s.offer(i).foreach {
        r =>
          println(s"[$d]Result $i: $r")
      }
  }
}
