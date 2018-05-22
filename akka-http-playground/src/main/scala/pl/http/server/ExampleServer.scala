package pl.http.server

import java.util.concurrent.atomic.AtomicLong

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.event.Logging.LogLevel
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshalling.{ToResponseMarshallable, ToResponseMarshaller}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import akka.util.{ByteString, Timeout}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.http.server.data.ANumber
import spray.json.DefaultJsonProtocol

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
