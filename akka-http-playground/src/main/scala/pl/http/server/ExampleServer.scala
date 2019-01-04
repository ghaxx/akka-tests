package pl.http.server

import java.util.concurrent.atomic.AtomicLong

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.Random

object ExampleServer extends App with CorsSupport with LazyLogging {
  implicit val system = ActorSystem("main-system", ConfigFactory.parseResources("akka-server.conf"))
  implicit val timeout = Timeout(3 seconds)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val counter = new AtomicLong()

  private def delayedComplete[T](duration: Int)(x: => T)(implicit marshaller: ToResponseMarshaller[T]) =
    complete {
      Thread.sleep(duration)
      x
    }

  private def requestDuration = 2000

  val dataStreaming = new DataStreaming

  val route =
    logRequestResult("Requests", Logging.InfoLevel) {
      path("random") {
        delayedComplete(requestDuration) {
          "" + Random.nextInt(100)
        }
      } ~ path("count") {
        delayedComplete(requestDuration) {
          "" + counter.getAndIncrement()
        }
      } ~ path("big") {
        complete((1 to (1024*1024*1024)).map(x => x + " ").mkString + "!!!")
      } ~ dataStreaming.route
    }

  val bindingFuture = Http().bindAndHandle(corsHandler(route), "localhost", 8080)

  logger.info("Server online at http://localhost:8080")
  logger.info("Press RETURN to stop")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
