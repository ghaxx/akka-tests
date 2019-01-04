package pl.tracing.kafka_tests

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage.CommittableOffset
import akka.kafka.scaladsl.Consumer.committableSource
import akka.kafka.scaladsl.Producer
import akka.kafka.{ConsumerMessage, ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions}
import akka.stream.ActorAttributes.SupervisionStrategy
import akka.stream.scaladsl.{Flow, Keep, Partition, Sink}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{ActorMaterializer, Attributes, FlowShape, Inlet, KillSwitches, Outlet, Supervision}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.slf4j.LoggerFactory
import pl.tracing.kafka_tests.NumbersAdvProcessor.logger

import scala.concurrent.Future
import scala.util.control.NonFatal

object NumbersAdvProcessor extends App {

  val logger = LoggerFactory.getLogger("println")

  implicit val system = ActorSystem("Processor")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val consumerConfig = system.settings.config.getConfig("kafka.consumer")
  private val consumerSettings = ConsumerSettings[String, String](consumerConfig, new StringDeserializer, new StringDeserializer)

  val producerConfig = system.settings.config.getConfig("kafka.producer")
  val producerSettings = ProducerSettings[String, String](producerConfig, new StringSerializer, new StringSerializer)
  val producer = Producer.flow[String, String, ConsumerMessage.CommittableOffset](producerSettings)


  val res = committableSource(consumerSettings, Subscriptions.topics("numbers"))
    .viaMat(KillSwitches.single)(Keep.both)
    .map {
      x =>
        val result = x.record.value().toInt
        logger.debug(s"Received message with offset: ${x.committableOffset.partitionOffset.offset} and value ${x.record.value()}")
        In(result, x.committableOffset)
    }
    .via(new ProcessorStage(i => i * i))
    .watchTermination(){
      (_, p) =>
        p.onComplete{
          _ =>
            logger.info("Stream done")
        }
    }
    .toMat(SinkFactory.get)(Keep.both)
    .run()

  case class In(n: Int, off: CommittableOffset)
  sealed trait Out {
    def off: CommittableOffset
  }
  case class Calc(n: Int, off: CommittableOffset) extends Out
  case class NoOp(n: Int, off: CommittableOffset) extends Out

  object SinkFactory {
    def get: Sink[Out, NotUsed] = {
      Sink.combine(publishingSink, ignoringSink)(i => Partition(2, {
        case _: Calc => 0
        case _: NoOp => 1
      }))
    }

    def publishingSink: Sink[Out, Future[Done]] = Flow[Out]
      .map {
        case Calc(n, off) =>
          val record = new ProducerRecord[String, String]("squares", n.toString)
          ProducerMessage.Message(record, off)
      }
      .toMat(Producer.commitableSink[String, String](producerSettings))(Keep.right)

    def ignoringSink: Sink[Out, Future[Done]] = Flow[Out]
      .mapAsync(1)(_.off.commitScaladsl())
      .toMat(Sink.ignore)(Keep.right)
  }

  class ProcessorStage(p: Int => Int) extends GraphStage[FlowShape[In, Out]] {
    val logger = LoggerFactory.getLogger("println")
    val in = Inlet[In]("Proc.in")
    val out = Outlet[Out]("Proc.out")
    val shape: FlowShape[In, Out] = FlowShape(in, out)
    def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
      new GraphStageLogic(shape) with InHandler with OutHandler {
        private def decider =
          inheritedAttributes.mandatoryAttribute[SupervisionStrategy].decider

        override def onPush(): Unit = {
          logger.info("onPush")
          try {
            grab(in) match {
              case In(n, _) if n % 100 == 0 =>
                logger.info(s"Got a 100, finishing stream")
                completeStage()
              case In(n, off) if n % 2 == 0 =>
                logger.info(s"Not processing $n")
                push(out, NoOp(-1, off))

              case In(n, off) if n % 2 != 0 =>
                logger.info(s"Processing $n")
                push(out, Calc(p(n), off))
            }
          } catch {
            case NonFatal(ex) ⇒ decider(ex) match {
              case Supervision.Stop ⇒
                logger.info(ex.getMessage, ex)
                failStage(ex)
              case _ ⇒ pull(in)
            }
          }
        }

        override def onPull(): Unit = pull(in)

        setHandlers(in, out, this)
      }
  }
}