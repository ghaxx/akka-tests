package pl.actors.dispatchers

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}


class SleepyRouter(dispatcherForWorkers: String) extends Actor with ActorLogging {

  def receive: Receive = {
    case x =>
      log.info(s"routing $x")
      router.route(x, sender())
  }

  private var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[SleepyReceiver].withDispatcher(dispatcherForWorkers))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }
}
