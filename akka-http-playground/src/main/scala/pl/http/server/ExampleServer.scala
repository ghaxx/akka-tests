package pl.http.server

import java.util.concurrent.atomic.AtomicLong

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.{ToResponseMarshallable, ToResponseMarshaller}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.Random

object ExampleServer extends App with LazyLogging {
  implicit val system = ActorSystem("main-system", ConfigFactory.parseResources("akka-server.conf"))
  implicit val timeout = Timeout(3 seconds)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val counter = new AtomicLong()

  private def delayedComplete[T](duration: Int)(x: => T)(implicit _marshaller: ToResponseMarshaller[T]) =
    complete {
    Thread.sleep(duration)
    x
  }
  private def requestDuration = 2000

  val route =
    logRequestResult("Requests") {
      path("random") {
        delayedComplete(requestDuration) {
          "" + Random.nextInt(100)
        }
      } ~ path("count") {
        delayedComplete(requestDuration) {
          "" + counter.getAndIncrement()
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  logger.info(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
