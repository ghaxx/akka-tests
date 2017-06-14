package pl.http.clients

import java.util.concurrent.Executors

import akka.util.ByteString
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
  implicit val timeout = 1 second
//  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())
//      implicit val executionContext = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(8))
//  val e = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(12))

  def makeRequest: Future[String] = {
    Http()
      .singleRequest(HttpRequest(uri = "http://localhost:8080/random"))
      .flatMap {
        r =>
          r.entity.dataBytes
            .runFold(ByteString.empty) { case (acc, b) => acc ++ b }
            .map {
              x => x.utf8String
            }
      }
//      .flatMap { r =>
//        r.entity.toStrict(3 seconds)
//      }
//      .map(e => e.getData.decodeString("UTF-8"))
  }

  val name = "akka http async"
  /**
    * For 50 requests:
    * Exception in thread "main" akka.stream.BufferOverflowException: Exceeded configured max-open-requests value of [32]
    */
  runTest()
  Thread.sleep(10000)
  Await.result(system.terminate(), 10 seconds)

}
