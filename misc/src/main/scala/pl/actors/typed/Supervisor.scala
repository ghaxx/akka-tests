package pl.actors.typed

import akka.actor.{Actor, Props}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

class Supervisor extends Actor {
  import Supervisor._
  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[Worker])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive: Receive = {
    case d: Delay =>
      router.route(d, sender())
//      println(s"sleeping for $millis")
//      Thread.sleep(millis)
//      println(s"sending answer: $millis")
//      sender ! s"slept for $millis"
    case a: Any => sender ! s"got '$a'!"
  }
}

object Supervisor {
  case class Delay(milliseconds: Int)
}