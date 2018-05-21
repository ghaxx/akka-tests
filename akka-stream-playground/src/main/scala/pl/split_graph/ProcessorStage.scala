package pl.split_graph

import akka.stream.ActorAttributes.SupervisionStrategy
import akka.stream.{Attributes, FlowShape, Inlet, Outlet, Supervision}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}

import scala.util.control.NonFatal

class ProcessorStage(processor: Processor) extends GraphStage[FlowShape[Input, Output]] {
  val in = Inlet[Input]("ProcessorStage.in")
  val out = Outlet[Output]("ProcessorStage.out")
  override val shape = FlowShape(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) with InHandler with OutHandler {
      private def decider =
        inheritedAttributes.mandatoryAttribute[SupervisionStrategy].decider

      override def onPush(): Unit = {
        try {
          grab(in) match {
            case Add(x, index) =>
              val result = processor.process(x)
              push(out, UpdateResult(result, index))
            case Update(x, index) =>
              processor.state = x
              push(out, NoOp(index))
          }
        } catch {
          case NonFatal(ex) ⇒ decider(ex) match {
            case Supervision.Stop ⇒ failStage(ex)
            case _                ⇒ pull(in)
          }
        }
      }

      override def onPull(): Unit = pull(in)

      setHandlers(in, out, this)
    }
}
