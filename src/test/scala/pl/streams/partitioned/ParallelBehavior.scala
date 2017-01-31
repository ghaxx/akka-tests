package pl.streams.partitioned

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestProbe
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.FlatSpec

trait ParallelBehavior {
  this: FlatSpec =>

  import scala.concurrent.duration._

  def parallelStreamHandlingErrors(graph: Graph[FlowShape[Msg, Msg], NotUsed])(implicit system: ActorSystem, mat: ActorMaterializer) = {

    implicit val executionContext = system.dispatcher

    it should "handle errors according to decider" in {
      val probe = TestProbe()
      Source
        .fromIterator(() => Seq(Msg(1), Msg(2), Msg(3), Msg(4)).iterator)
        .via(graph)
        .withAttributes(ActorAttributes.withSupervisionStrategy(Supervision.resumingDecider))
        .runWith(Sink.actorRef(probe.ref, "completed"))

      probe.expectMsgAllOf(1.second, Msg(2), Msg(3), Msg(4))
      probe.expectMsg(1.second, "completed")
    }
  }

  def parallelStream(graph: Graph[FlowShape[Msg, Msg], NotUsed])(implicit system: ActorSystem, mat: ActorMaterializer) = {

    implicit val executionContext = system.dispatcher

    it should "complete on source complete" in {
      val probe = TestProbe()
      Source.single(Msg(2))
        .via(graph)
        .runWith(Sink.actorRef(probe.ref, "completed"))

      probe.expectMsg(2.second, Msg(2))
      probe.expectMsg(2.second, "completed")
    }

    it should "handle multiple elements simultaneously" in {
      val probe = TestProbe()
      Source
        .fromIterator(() => Seq(Msg(4), Msg(1), Msg(2), Msg(3)).iterator)
        .withAttributes(ActorAttributes.supervisionStrategy(loggingDecider))
        .via(graph)
        .runWith(Sink.actorRef(probe.ref, "completed"))

      probe.expectMsgAllOf(5.second, Msg(1), Msg(2), Msg(3), Msg(4))
      probe.expectMsg(5.second, "completed")
    }

    it should "use one partition for the same key" in {
      Source.fromIterator(() => Seq(Msg(1), Msg(1), Msg(3), Msg(4)).iterator)
        .via(graph)
        .withAttributes(ActorAttributes.withSupervisionStrategy(Supervision.resumingDecider))
        .runWith(TestSink.probe[Msg])
        .request(4)
        .expectNext(Msg(1), Msg(3), Msg(4))
        .expectNext(Msg(1))
        .request(1)
        .expectComplete()
    }
  }

  private val loggingDecider: Supervision.Decider = new Supervision.Decider with LazyLogging {
    override def apply(throwable: Throwable): Supervision.Directive = {
      logger.error(throwable.getMessage, throwable)
      Supervision.resumingDecider(throwable)
    }
  }

}
