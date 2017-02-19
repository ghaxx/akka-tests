package pl.http.clients

import java.util.concurrent.Executors

import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

object AkkaHttpClient extends App with ClientTestScenario {
  import akka.actor.ActorSystem
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model._
  import akka.stream.ActorMaterializer

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
      implicit val executionContext = system.dispatcher
//  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())
//      implicit val executionContext = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(8))
//  val e = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(12))

  def makeRequest: Future[String] = {
    Http()
      .singleRequest(HttpRequest(uri = "http://localhost:8080/random"))
      .flatMap { r =>
        println("done")
        r.entity.toStrict(3 seconds)
      }
      .map(e => e.getData.decodeString("UTF-8"))
  }

  val name = "akka http async"
  runTest()
  Await.result(system.terminate(), 10 seconds)

}
