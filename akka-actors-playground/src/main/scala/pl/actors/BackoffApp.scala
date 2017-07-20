package pl.actors

import java.io.{ByteArrayOutputStream, ObjectOutputStream}

import akka.actor.SupervisorStrategy.Directive
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.{Backoff, BackoffSupervisor}
import akka.stream.Supervision.Restart
import com.typesafe.config.ConfigFactory

import scala.concurrent.{Await, Future}
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object BackoffApp extends App {

  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  val system = ActorSystem("Test-Akka-Backoff")

  def supervisor(other: String) = BackoffSupervisor.props(
    Backoff
        .onFailure(
          Props(new B(other)),
          childName = "myEcho",
          minBackoff = 1 second,
          maxBackoff = 5 seconds,
          randomFactor = 0.2 // adds 20% "noise" to vary the intervals slightly
        )
        .withSupervisorStrategy(
          OneForOneStrategy() {
            case _ =>
              println("Restarting")
              SupervisorStrategy.Restart
          }
        )
  )

  //  val actor = system.actorOf(Props[B])
  //  val actorA = system.actorOf(supervisor("B"), "bA")
  //  val actorB = system.actorOf(supervisor("A"), "bB")
  val sup = system.actorOf(Props(new Actor with ActorLogging {

    override def preStart() = {
      log.info(s"Starting sup")
      val actorA = context.actorOf(Props(new B("user/sup/bB")), "bA")
      val actorB = context.actorOf(Props(new B("user/sup/bA")), "bB")
    }
    override def receive = {
      case x => log.info(s"x")
    }

    override def supervisorStrategy = OneForOneStrategy() {
      case _ => SupervisorStrategy.Resume
    }
  }), "sup")
  //  val actorA = system.actorOf(Props(new B("B")), "bA")
  //  val actorB = system.actorOf(Props(new B("A")), "bB")
  Thread.sleep(1000)
  system.actorSelection("user/sup/bA") ! Do(0)

  Thread.sleep(20000)

  class B(other: String) extends Actor with ActorLogging {

    override def preStart() = {
      super.preStart()
      log.info(s"*** Pre start ${self.path}")
    }

    override def postStop() = {
      log.info(s"+++ Post stop ${self.path}")
      super.postStop()
    }

    override def preRestart(reason: Throwable, message: Option[Any]) = {
      super.preRestart(reason, message)
      log.info(s">>> Pre restart ${self.path}: $message")
      Thread.sleep(2000)
      log.info(">>> Run!")
    }

    override def postRestart(reason: Throwable) = {
      log.info(s"<<< Pre restart ${self.path}")
      super.postRestart(reason)
    }

    override def receive: Receive = {
      case x @ Do(i) =>
        log.info(s"Actor ${self.path} received: $x")
        //        context.system.actorSelection("user/b" + (if(i%2==1)"A" else "B")).resolveOne(100 millis).foreach {
        //          a =>
        //              log.info(s"Sending ${x.next} from ${self.path} to ${a.path}")
        //            context.system.scheduler.scheduleOnce(500 millis, a, x.next)
        //        }
        //        context.system.scheduler.scheduleOnce(500 millis, new Runnable() {
        //          override def run() = {
        //            val path = s"user/b$other"
        //            log.info(s"Sending ${x.next} from ${self.path} to ${path}")
        //            context.system.actorSelection(path) ! x.next
        //          }
        //        })
        Future {
          Thread.sleep(500)
          log.info(s"Sending ${x.next} from ${self.path} to ${other}")
          context.system.actorSelection(other) ! x.next
        }
        //        context.system.scheduler.scheduleOnce(500 millis, sender(), x.next)
        if (i % 5 == 4)
          throw new RuntimeException("Stop " + self.path)
    }
  }

  case class Do(i: Int) {
    def next = Do(i + 1)
  }

}
