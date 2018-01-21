package pl.tracing

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge, Sink, Source}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, FlowShape, OverflowStrategy}
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, Future}

object SourceQueueTest extends App {

  import scala.concurrent.duration._

  implicit val system = ActorSystem("main-system")
  implicit val timeout = Timeout(3 seconds)
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(16))
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system).withInputBuffer(initialSize = 1, maxSize = 32))

  val st = System.currentTimeMillis()
  def d = f"${(System.currentTimeMillis() - st) % 100000}%05d"


  val s = Source.queue[Int](0, OverflowStrategy.dropNew)
//      .async
    .mapAsyncUnordered(20) {
      e => Future {
        println(s"[$d]Processing: $e")
        Thread.sleep(1000)
        e
      }
    }
//    .async
    .map {
      e =>
        println(s"[$d]Processed: $e")
        e

    }
//    .async
    .to(Sink.foreach {
      e =>
        println(s"[$d]Consumed: $e")
    })
//    .async
    .run()

  val s2 = Source.queue[Int](0, OverflowStrategy.dropNew)
      .async
      .via(Flow.fromGraph(GraphDSL.create() {
        implicit b =>
          import GraphDSL.Implicits._
          val balance = b.add(Balance[Int](20))
          val merge = b.add(Merge[Int](20))
          (0 until 20) foreach { i =>
            balance.out(i) ~> Flow[Int].mapAsync(1) {
              e =>
                println(s"[$d]Processing in $i: $e")
                Thread.sleep(e * 1000)
                Future successful e
            }.async ~> merge.in(i)
          }
          FlowShape(balance.in, merge.out)
      }))
    .map {
      e =>
        println(s"[$d]Processed: $e")
        e

    }
    .to(Sink.foreach {
      e =>
        println(s"[$d]Consumed: $e")
    }).async.run()

  val s3 = Source.queue[Int](0, OverflowStrategy.dropNew)
    .map {
      e =>
        println(s"[$d]Processing: $e")
        Thread.sleep(e * 1000)
        e
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

  (1 to 100) foreach {
    i =>
      println(s"[$d]Offering $i")
      s.offer(i).foreach {
        r =>
          println(s"[$d]Result $i: $r")
      }
  }
}
