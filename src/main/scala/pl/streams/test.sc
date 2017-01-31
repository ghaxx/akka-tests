import akka.stream.{Attributes, SourceShape}
import akka.stream.scaladsl.Source
import akka.stream.stage.{GraphStage, GraphStageLogic}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

val s = Source(1 to 3)
val s1 = Source.single(Future("single value from a Future"))

class A extends GraphStage[SourceShape[Int]] {
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = ???

  override def shape: SourceShape[Int] = ???
}