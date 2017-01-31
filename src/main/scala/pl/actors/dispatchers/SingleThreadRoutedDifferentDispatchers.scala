package pl.actors.dispatchers

import java.util.concurrent.Executors

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import pl.actors.typed.Worker

import scala.concurrent.ExecutionContext

object SingleThreadRoutedDifferentDispatchers extends App {

  implicit val system = ActorSystem()

  def tryDispatcher(name: String) = {
    println(name)
    val receiver = system.actorOf(Props(new SleepyRouter(name)))

    receiver ! 1000
    receiver ! 1001

    Thread.sleep(2500)
  }

  tryDispatcher("single-thread-dispatcher")
  tryDispatcher("multi-thread-dispatcher")

  system.terminate()

}