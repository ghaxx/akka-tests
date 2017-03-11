package pl.actors.typed

import akka.actor.Actor

class Worker extends Actor {
  import Supervisor._
  def receive: Receive = {
    case Delay(2000) =>
      throw new RuntimeException("2000")
    case Delay(millis) =>
      println(s"sleeping for $millis")
      Thread.sleep(millis)
      println(s"sending answer: $millis")
      sender ! s"slept for $millis"
  }
}
