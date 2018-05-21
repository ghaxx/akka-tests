package pl.partitioned

import java.util.concurrent.Executors

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorAttributes.SupervisionStrategy
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Attributes, FlowShape, Inlet, Outlet, Supervision}
import akka.util.Timeout
import pl.MySpec
import pl.tracing.parallelism.MapAsyncToWorkers

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success
import scala.util.control.NonFatal

class SplittingTest extends MySpec {

  import scala.concurrent.duration._

  implicit val system = ActorSystem("main-system")
  implicit val timeout = Timeout(3 seconds)
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system).withInputBuffer(initialSize = 8, maxSize = 64))

  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(4))

  def source: Source[Msg, NotUsed] = Source.unfold(0) {
    i =>
      Some(i + 1, Msg(i))
  }
    .take(160).async

  "it" should "split with group by and be slowest" in {
    log("In 1")
    source
      .groupBy(2, _.i % 2).async
      .map(process)
      .mergeSubstreams
      .runWith(Sink.foreach(m => log(m.toString))).futureValue
  }

  it should "split with unique async workers" in {
    log("split with unique async workers")
    source
      .via(MapAsyncToWorkers(2, processF, _.i)).async
      .runWith(Sink.foreach(m => log(m.toString))).futureValue
  }

  it should "split with mapAsync and be fastest but async" in {
    log("In 2")
    source
      .mapAsync(2)(processF).async
      .runWith(Sink.foreach(m => log(m.toString))).futureValue
  }

  it should "split with unique workers" in {
    log("In 3")
    source
      .via(MapUniqueGroup(2, process, _.i)).async
      .runWith(Sink.foreach(m => log(m.toString))).futureValue
    Thread.sleep(1000)
  }

  def process(msg: Msg): Msg = {
    msg.sleep
    msg
  }

  def processF(m: Msg): Future[Msg] = Future(process(m))
//dfgdfgdfgdg
  case class MapUniqueGroup[In, Out](parallelism: Int, f: In => Out, partitionKey: In => Int) extends GraphStage[FlowShape[In, Out]] {

    private val in = Inlet[In]("in")
    private val out = Outlet[Out]("out")
    override val shape: FlowShape[In, Out] = FlowShape(in, out)

    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
      new GraphStageLogic(shape) with InHandler with OutHandler {
        private def decider =
          inheritedAttributes.mandatoryAttribute[SupervisionStrategy].decider
        val buffer = new mutable.Queue[In]()
        val outBuffer = new mutable.Queue[Out]()
        implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(parallelism + 3))
        val ec2 = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(3))

        def run(e: In): Unit = {
          log(s"working with $e")
          Future {
            log("enqueing")
            f(e)
          }.onComplete{
            case Success(x) =>
            outBuffer.enqueue(x)
              pushNextOrPull()
            }(ec2)
        }

        override def onPush(): Unit = {
          log("on push")
          try {
            run(grab(in))
          } catch {
            case NonFatal(ex) ⇒ decider(ex) match {
              case Supervision.Stop ⇒ failStage(ex)
              case _ ⇒ pull(in)
            }
          }
        }

        def pushNextOrPull() = {
          log("push next or pull")
          if (outBuffer.nonEmpty) {
            log("deq")
            val x = outBuffer.dequeue()
            if (isAvailable(out)) {
              log(s"push $x")
              push(out, x)
              pull(in)
            } else {
              outBuffer.enqueue(x)
            }
          } else {
            log("pull")
            tryPull(in)
          }
        }

        override def onPull(): Unit = {
          log("on pull")
          pushNextOrPull()
        }

        setHandlers(in, out, this)
      }
  }

  case class Msg(i: Int) {
    def sleep() = {
      if (i % 2 == 1)
        Thread.sleep(10)
      else if (i % 4 == 2)
        Thread.sleep(100)
      else if (i % 4 == 0)
        Thread.sleep(90)
      else
        throw new RuntimeException(s"Uncought i = $i")
    }
  }
}
