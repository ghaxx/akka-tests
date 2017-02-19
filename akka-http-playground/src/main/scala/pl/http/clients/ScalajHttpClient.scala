package pl.http.clients

import java.util.concurrent.Executors

import pl.performance.Timer

import scala.concurrent.{Await, ExecutionContext, Future}
import scalaj.http._
import scalaz.Scalaz._
import scala.concurrent.duration._

object ScalajHttpClient extends App with ClientTestScenario {
//  implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))
  implicit val ec = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
  val request: HttpRequest = Http("http://localhost:8080/random")

  def makeRequest: Future[String] = Future {
    request.asString.body
  }

  val name = "scalaj async"
  runTest()
}
