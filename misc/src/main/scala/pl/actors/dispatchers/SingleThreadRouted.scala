package pl.actors.dispatchers

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import pl.actors.typed.Worker

object SingleThreadRouted extends App {

  implicit val system = ActorSystem()

  val receiver = system.actorOf(Props(new SleepyRouter("single-thread-dispatcher")))

  receiver ! 1000
  receiver ! 1001

  //  system.terminate()

}