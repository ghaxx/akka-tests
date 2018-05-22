package pl.http.server

import java.util.concurrent.atomic.AtomicLong

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.BasicDirectives.extractRequestContext
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn

object ExampleGateway extends App with CorsSupport with LazyLogging {
  implicit val system = ActorSystem("main-system", ConfigFactory.parseResources("akka-server.conf"))
  implicit val timeout = Timeout(3 seconds)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val counter = new AtomicLong()

  def process(prefix: String, port: Int): Route =
    pathPrefix(prefix) {
      path(RemainingPath) { tail =>
        Route { context =>
          val request = context.request
          println("Opening connection to " + request.uri.authority.host.address)
          val flow = Http(system).outgoingConnection(request.uri.authority.host.address(), port)
          val handler = Source.single(context.request)
            .map { request =>
              val oltPath = request.uri.path
              val newPath = tail
              println(s"$oltPath -> $newPath")
              request.withUri(request.uri.withPath(Path.Slash(newPath)))
            }
            .via(flow)
            .runWith(Sink.head)
            .flatMap(context.complete(_))
          handler
        }
      }
    }

  val proxyRoute =
        process("test", 80) ~
          process("test2", 81)

  val targetRoute = extractRequestContext { request =>
    pathPrefix("target") {
      println(request.toString)
      complete(request.toString)
    } ~ complete("other")
  }

  val target2Route = extractRequestContext { request =>
    pathPrefix("target2") {
      println(request.toString)
      complete(request.toString)
    }  ~ complete("other2")
  }

  val servers = Future.sequence(List(
    Http().bindAndHandle(corsHandler(proxyRoute), "localhost", 8080),
    Http().bindAndHandle(corsHandler(targetRoute), "localhost", 80),
    Http().bindAndHandle(corsHandler(target2Route), "localhost", 81)
  ))

  logger.info("Server online at http://localhost:8080")
  logger.info("Press RETURN to stop")
  StdIn.readLine()
  servers
    .flatMap(x => Future.sequence(x.map(y => y.unbind())))
    .onComplete(_ => system.terminate())
}
