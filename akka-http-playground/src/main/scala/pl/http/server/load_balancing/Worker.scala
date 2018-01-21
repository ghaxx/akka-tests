package pl.http.server.load_balancing

import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Directives.{complete, logRequestResult, path}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import pl.http.server.load_balancing.WorkerApp.getClass

import scala.util.Random

class Worker(configPath: String) {
  import scala.concurrent.duration._
  implicit val system = ActorSystem("main-system", ConfigFactory.parseResources(getClass, configPath))
  implicit val timeout = Timeout(3 seconds)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  system.actorOf(Props(new WorkerActor(configPath)), "worker")
}
