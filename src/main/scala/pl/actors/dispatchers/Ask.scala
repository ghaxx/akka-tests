package pl.actors.dispatchers

import java.util.concurrent.Executors

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import org.slf4j.LoggerFactory
import pl.performance.Timer

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object Ask extends App {

  implicit val system = ActorSystem()
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(8))
  implicit val timeout = Timeout(5 seconds)

  val receiver = system.actorOf(Props(new SleepyRouter("single-thread-dispatcher")), "router")

  val r1 = (receiver ? 1000).mapTo[Int]
  val r2 = (receiver ? 1001).mapTo[Int]

  val t = Timer("time")
  for {
    a <- r1
    b <- r2
  } yield {
    println(s"a + b = ${a + b}")
    println(t.status)
  }

  Thread.sleep(5000)
}
