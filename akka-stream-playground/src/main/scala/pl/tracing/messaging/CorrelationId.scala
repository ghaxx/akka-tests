package pl.tracing.messaging

import scala.util.Random

case class CorrelationId private(value: Int) extends AnyVal

object CorrelationId {
  def createNew = CorrelationId(10000000 + Random.nextInt(10000000))
}

