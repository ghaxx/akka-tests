package pl.tracing.kafka_tests

import akka.stream.{Attributes, Inlet, SinkShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger


class LoggingSink(logger: Logger) extends GraphStage[SinkShape[Any]] {
  val in: Inlet[Any] = Inlet("StdoutSink")
  override val shape: SinkShape[Any] = SinkShape(in)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      // This requests one element at the Sink startup.
      override def preStart(): Unit = pull(in)

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          logger.debug("" + grab(in))
          pull(in)
        }
      })
    }
}