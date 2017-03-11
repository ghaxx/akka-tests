package pl.actors.dispatchers

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

object SingleThreadSingleActor extends App {

  implicit val system = ActorSystem()

  val receiver = system.actorOf(Props[SleepyReceiver])

  receiver ! 1000
  receiver ! 1001

  Thread.sleep(2500)
  system.terminate()

}
