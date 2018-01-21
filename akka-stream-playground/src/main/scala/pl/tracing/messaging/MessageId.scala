package pl.tracing.messaging

import scala.util.Random

case class MessageId(value: Int) extends AnyVal

object MessageId {
  def createNew = MessageId(10000000 + Random.nextInt(10000000))
}

