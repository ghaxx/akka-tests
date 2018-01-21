package pl.http.server.load_balancing

import java.util.concurrent.atomic.AtomicLong

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives.{complete, logRequestResult, path}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.http.server.{CorsSupport, DataStreaming}
import pl.http.server.ExampleServer.{corsHandler, logger}

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.Random
import akka.pattern.ask

object Gateway extends App with CorsSupport with LazyLogging {
  import scala.concurrent.duration._
  implicit val system = ActorSystem("main-system", ConfigFactory.parseResources(getClass, "gateway.conf"))
  implicit val timeout = Timeout(3 seconds)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val counter = new AtomicLong()


  val dataStreaming = new DataStreaming

  system.actorOf(Props[SimpleClusterListener])

  val handler: HttpRequest => Future[HttpResponse] = {
    case r: HttpRequest =>
      (system.actorSelection("akka.tcp://main-system@localhost1:2551/user/worker") ? r).mapTo[HttpResponse]
  }

  val bindingFuture = Http().bindAndHandleAsync(handler, "localhost", 8080)

  logger.info("Server online at http://localhost:8080")
  logger.info("Press RETURN to stop")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
