package pl.http.server.streaming_tests

import java.util.concurrent.atomic.AtomicLong

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Directives.{complete, logRequestResult, path}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, MergeHub, Sink, Source}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.http.server.{CorsSupport, DataStreaming}
import pl.http.server.ExampleServer.{corsHandler, logger}

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.Random
import scala.concurrent.duration._

object Server extends App with LazyLogging {
  import akka.http.scaladsl.server.Directives._
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
var c = 0
  val waiter = Sink.fold(0) {(_: Int, t: Int) =>
    Thread.sleep(t)
    println(s"$t")
    t
  }
  val m = MergeHub.source[Int].to(waiter).run()
  val n = Flow[Int].map({t: Int =>
    Thread.sleep(t)
    println(s"$t")
    "" + t
  })//.to(Sink.head)
  val s = Source.queue(1, OverflowStrategy.fail).to(waiter).run()

  val route =
//    logRequestResult("Requests", Logging.InfoLevel) {
      path("invert" / Segment) { text =>
        delayedComplete(requestDuration) {
          text.map {
            case char if char.isUpper => char.toLower
            case char if char.isLower => char.toUpper
            case char => char
          }.mkString("")
        }
      } ~ path("append" / Segment) { text =>
        delayedComplete(requestDuration) {
          text + "!"
        }
      } ~ pathPrefix("hub") {
        path("wait" / IntNumber) { n =>
          complete {
            val a = Source.single(n).runWith(m)
//            a.map(_.toString)
            "OK"
          }
        }
      }~ pathPrefix("queue") {
        path("wait" / IntNumber) { n =>
          complete {
            c += 1
            val r = s.offer(n+c)
//            r.map(_.toString)
            "" + n
          }
        }
      }~ pathPrefix("single") {
        path("wait" / IntNumber) { nun =>
          complete {
            c += 1
            val t: Future[String] = Source.single(nun+c).via(n).runWith(Sink.head)
            t
          }
        }
      }
//    }

  val bindingFuture = Http().bindAndHandle(corsHandler(route), "localhost", 8080)

  logger.info("Server online at http://localhost:8080")
  logger.info("Press RETURN to stop")

  Stream.from(1).map{
    i => Thread.sleep(i*10)
      i
  }.take(100).foreach(s.offer)

  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
