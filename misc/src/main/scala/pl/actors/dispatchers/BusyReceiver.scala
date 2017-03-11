package pl.actors.dispatchers

import akka.actor.{Actor, ActorLogging}


class BusyReceiver extends Actor with ActorLogging {
  def receive: Receive = {
    case x: Int =>
      log.info(s"${self.path} starts processing $x")
      for(i <- 1 to Int.MaxValue >> 3) {
        math.asin(i)
      }
      log.info(s"${self.path} processed $x")
      sender ! x
  }
}