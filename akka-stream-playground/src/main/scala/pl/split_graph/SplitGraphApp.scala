package pl.split_graph

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, DelayOverflowStrategy, FlowShape}
import akka.stream.scaladsl._
import org.slf4j.LoggerFactory
import pl.tracing.SourceQueueTest.{d, s}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object SplitGraphApp extends App {

  import concurrent.ExecutionContext.Implicits.global
  import DelayingStream._

  val logger = LoggerFactory.getLogger("println")

  def streamOfNumbers = Stream.from(7).zipWithIndex
  val sourceForAdding = Source(streamOfNumbers.delayElements(100 millis)).map(x => Add(x._1, x._2))
  val sourceForUpdating = Source(streamOfNumbers.delayElements(100 millis)).map(x => Update(x._1, x._2))



  implicit val system = ActorSystem("stream")
  implicit val materializer = ActorMaterializer()

  val processor = new Processor(0)

//  Source.combine(sourceForAdding, sourceForUpdating)(_ => MergePrioritized(Seq(4, 1), eagerComplete = false))
//    .via(new ProcessorStage(processor))
//    .runWith(Sink.foreach(x => logger.info(s"Finished with $x")))

  val flow = Flow.fromGraph(GraphDSL.create() {
    implicit b =>
      import GraphDSL.Implicits._
      val fanOut = b.add(new ProcessorFanOutStage(processor))
      fanOut.out1 ~> Flow[NoOp].map{x => Thread.sleep(10000); x} ~> Sink.foreach[NoOp](x => logger.info(s"Dropped $x"))
      FlowShape(fanOut.in, fanOut.out0)
  })

  Source.combine(sourceForAdding, sourceForUpdating)(_ => MergePrioritized(Seq(4, 1), eagerComplete = false))
    .via(flow)
    .runWith(Sink.foreach(x => logger.info(s"Finished with $x")))

//  Thread.sleep(1000)

//  Await.result(system.terminate(), 5 seconds)


}
