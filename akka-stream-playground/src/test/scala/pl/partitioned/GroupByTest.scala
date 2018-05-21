package pl.partitioned

import akka.actor.ActorSystem
import akka.stream.{ActorAttributes, ActorMaterializer, Supervision}
import akka.stream.scaladsl._
import pl.MyLittleHelper.Timer
import pl.MySpec

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import Global._
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestProbe
import pl.tracing.parallelism.MapAsyncToWorkers

class GroupByTest extends MySpec with ParallelBehavior {

  implicit val system = ActorSystem("test")
  implicit val executionContext = system.dispatcher
  //  implicit val executionContext = system.dispatchers.lookup("my-dispatcher")
  implicit val materializer = ActorMaterializer.create(system)

  val messages = List(Msg(1, 2), Msg(1, 0), Msg(1, 1), Msg(2, 1), Msg(3, 1), Msg(3, 0), Msg(3, 1), Msg(4, 4)) :::
    (for {
      i <- 1 to 1000
//      j <- 1 to 2
      cls <- List(Msg(i, 5), Msg(i + 1, 5), Msg(i + 2, 5), Msg(i + 3, 5))
    } yield cls).toList

  val asyncFlow = Flow[Msg].mapAsync(1) {
    s ⇒ Future {
      s.work
    }
  }

  val syncFlow = Flow[Msg].map { s =>
    s.work
  }

  val parallelism = 50

  def asyncStream = Source(messages)
    .groupBy(parallelism, cls ⇒ cls.key(parallelism))
    .via(asyncFlow)
    .mergeSubstreams

  def syncStream = Source(messages)
    .groupBy(parallelism, cls ⇒ cls.key(parallelism))
    .via(syncFlow.async)
    .mergeSubstreams

  def customStream = Source(messages)
    .via(MapAsyncToWorkers(parallelism, s => Future(s.work), s => s.key(parallelism), 20))


  //  "Stream" should "handle multiple elements simultaneously" ignore {
  //    p("it should handle multiple elements simultaneously")
  //    p(s"total time = ${messages.map(_.sleepTime).sum}")
  //    p(s"expected time = ${messages.groupBy(_.id).mapValues(_.map(_.sleepTime).sum).values.max}")
  //    val probe = TestProbe()
  //    val s = syncStream.runWith(Sink.actorRef(probe.ref, "completed"))
  //
  //    probe.expectMsgAllOf(3 seconds, Cls(1, 1), Cls(1, 0), Cls(3, 3), Cls(4, 4))
  //    probe.expectMsg(3 seconds, "completed")
  //  }

  "Test" should "show times" ignore {
    println("it should handle multiple elements simultaneously")
    println(s"total time = ${messages.map(_.sleepTime).sum}")
    println(s"optimistic time = ${messages.groupBy(_.key(parallelism)).mapValues(_.map(_.sleepTime).sum).values.max}")

    val r = 5
    repeat(r) {
      logTime("sync") {
        val syncResult = syncStream
          .runForeach { s ⇒
            p(s"[${Global.timer.status}] Finished $s")
          }
        Await.result(syncResult, 60 seconds)
      }
    }

    repeat(r) {
      logTime("custom") {
        val customResult = customStream
          .runForeach { s ⇒
            p(s"[${Global.timer.status}] Finished $s")
          }
        Await.result(customResult, 60 seconds)
      }
    }
  }
  //
  //  it should "handle errors according to decider" ignore {
  //    val probe = TestProbe()
  //    customStream.withAttributes(ActorAttributes.withSupervisionStrategy(Supervision.resumingDecider))
  //      .runWith(Sink.actorRef(probe.ref, "completed"))
  //
  //    probe.expectMsgAllOf(6 second, 2, 3, 4)
  //    probe.expectMsg(7 second, "completed")
  //  }
  //
  //  it should "use one partition for the same key" in {
  //    p("it should use one partition for the same key")
  //
  //    asyncStream.runWith(TestSink.probe[Cls])
  //      .request(4)
  //      .expectNext(Cls(1, 1), Cls(3, 2), Cls(4, 3))
  //      .expectNext(Cls(1, 0))
  //      .request(1)
  //      .expectComplete()
  //  }

  it should "handle multiple elements simultaneously" ignore {
    val probe = TestProbe()
    Source.fromIterator(() => Seq(1, 1, 3, 4).iterator)
      .groupBy(3, s ⇒ s % 3)
      .via(Flow[Int].map(s => {
        Thread.sleep(350 + s)
        s
      }).async)
      .mergeSubstreams
      .runWith(Sink.actorRef(probe.ref, "completed"))

    probe.expectMsgAllOf(1.second, 1, 1, 3, 4)
    probe.expectMsg(1.second, "completed")
  }

  it should "handle errors according to decider" ignore {
    val probe = TestProbe()
    Source.fromIterator(() => Seq(1, 2, 3, 4).iterator)
      .groupBy(6, s ⇒ s)
      .via(Flow[Int].map(s => {
        if (s == 1) throw new IllegalArgumentException
        else s
      }).withAttributes(ActorAttributes.withSupervisionStrategy(Supervision.resumingDecider)).async)
      .mergeSubstreams
      .runWith(Sink.actorRef(probe.ref, "completed"))

    probe.expectMsgAllOf(1.second, 2, 3, 4)
    probe.expectMsg(1.second, "completed")
  }

  it should "use one partition for the same key" ignore {

    Source.fromIterator(() => Seq(1, 1, 3, 4).iterator)
      .groupBy(3, s ⇒ s % 3)
      .via(Flow[Int].map(i => {
        Thread.sleep(400 + 100 * i)
        i
      }).async)
      .mergeSubstreams
      .withAttributes(ActorAttributes.withSupervisionStrategy(Supervision.resumingDecider))
      .runWith(TestSink.probe[Int])
      .request(4)
      .expectNext(1, 3, 4)
      .expectNext(1)
      .request(1)
      .expectComplete()
  }

  "Group by" should behave like parallelStream {
    Flow[Msg]
      .groupBy(3, s ⇒ s.id)
      .map(s => s.work)
      .async
      .mergeSubstreams
  }

}
