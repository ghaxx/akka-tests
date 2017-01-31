package pl.actors.dispatchers

import akka.actor.{Actor, ActorLogging}


class SleepyReceiver extends Actor with ActorLogging {
  def receive: Receive = {
    case x: Int =>
      log.info(s"${self.path} starts sleeping $x")
      Thread.sleep(x)
      log.info(s"${self.path} woken up $x")
      sender ! x
  }
}