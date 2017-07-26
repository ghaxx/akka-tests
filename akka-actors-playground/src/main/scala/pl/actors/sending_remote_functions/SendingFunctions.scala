package pl.actors.sending_remote_functions

import java.io.{ByteArrayOutputStream, ObjectOutputStream}

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object SendingFunctions extends App {

  val system = ActorSystem("Test-Akka-2", ConfigFactory.parseResources("pl/actors/sending_remote_functions/akka-remote2.conf"))
  system.actorOf(Props[B], "b")
  system.actorSelection("akka.tcp://Test-Akka-2@127.0.0.1:2552/user/b") ! "ping"
  val actor = system.actorSelection("akka.tcp://Test-Akka@127.0.0.1:2551/user/a")

  val bos = new ByteArrayOutputStream
  val out = new ObjectOutputStream(bos)
  out.writeObject((x => x*x): Int => Int)
  out.close()
  val s = new String(bos.toByteArray)
  println(s"""s = ${s}""")
  actor ! 7
  actor ! ((x => x*x): Int => Int)

  class B extends Actor with ActorLogging {
    override def receive: Receive = {
      case x =>
        log.info(s"$x")
    }
  }

}
