package pl.actors.typed

import akka.actor.Actor

class Asker extends Actor {
  def receive: Receive = {
    case "start" => context.system.actorSelection("user/supervisor") ! "hi!"
    case a: Any =>
      println(a)
  }
}
