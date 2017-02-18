package pl.http.clients

import java.util.concurrent.Executors

import org.slf4j.LoggerFactory
import pl.performance.Timer

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scalaz.Scalaz._

object AkkaHttpClient extends App {

  val logger = LoggerFactory.getLogger(AkkaHttpClient.getClass)

  import akka.actor.ActorSystem
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model._
  import akka.stream.ActorMaterializer

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  //  implicit val executionContext = system.dispatcher
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())
  //  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(8))

  def asyncResponse = {
    Http()
      .singleRequest(HttpRequest(uri = "http://localhost:8080/random"))
      .flatMap(r => r.entity.toStrict(3 seconds))
      .map(e => e.getData.decodeString("UTF-8"))
  }

  val t = Timer("akka http async")
  println("threads: " + Thread.activeCount())
  val fResponses = (1 to 30).map {
    _ => asyncResponse
  }
  println("threads: " + Thread.activeCount())
  fResponses.foreach {
    r => Await.result(r, 10 seconds) |> println
  }
  println("threads: " + Thread.activeCount())
  println(t.status)
}
