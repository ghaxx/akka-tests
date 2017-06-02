package pl.actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import pl.actors.SendingFunctions.system

object ReceivingFunctions extends App {

  val system = ActorSystem("Test-Akka", ConfigFactory.parseResources("akka-remote.conf"))
  system.actorOf(Props[A], "a")
  system.actorSelection("akka.tcp://Test-Akka@127.0.0.1:2551/user/a") ! "ping"

  class A extends Actor with ActorLogging {
    override def receive: Receive = {
      case x: String => log.info(s"String: $x")
      case x: Int => log.info(s"Int: $x")
      case x: (Int => Int) => log.info("Int => Int: " + x(10))
    }
  }



}
