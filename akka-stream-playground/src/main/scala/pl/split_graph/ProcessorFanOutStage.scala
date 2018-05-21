package pl.split_graph

import akka.stream.ActorAttributes.SupervisionStrategy
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{Attributes, FanOutShape2, FlowShape, Inlet, Outlet, Supervision}
import org.slf4j.LoggerFactory

import scala.util.control.NonFatal

class ProcessorFanOutStage(processor: Processor) extends GraphStage[FanOutShape2[Input, UpdateResult, NoOp]] {
  override val shape = new FanOutShape2[Input, UpdateResult, NoOp]("FanOut")

  val logger = LoggerFactory.getLogger("println")

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      private def decider =
        inheritedAttributes.mandatoryAttribute[SupervisionStrategy].decider

      var demand = false

      setHandler(shape.in, new InHandler {
        def onPush(): Unit = {
          try {
            logger.info("Push")
            grab(shape.in) match {
              case Add(x, index) =>
                val result = processor.process(x)
                demand = false
                push(shape.out0, UpdateResult(result, index))
              case Update(x, index) =>
                processor.state = x
                demand = false
                push(shape.out1, NoOp(index))
            }
          } catch {
            case NonFatal(ex) ⇒ decider(ex) match {
              case Supervision.Stop ⇒ failStage(ex)
              case _                ⇒ pull(shape.in)
            }
          }
        }
      })

      setHandler(shape.out0, new OutHandler{
        def onPull(): Unit = {
          if (!demand) {
            demand = true
            logger.info("Pull 0")
            pull(shape.in)
          }
        }
      })

      setHandler(shape.out1, new OutHandler{
        def onPull(): Unit = {
          if (!demand) {
            demand = true
            logger.info("Pull 1")
            pull(shape.in)
          }
        }
      })
    }
}
