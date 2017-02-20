package pl.http.clients

import java.util.concurrent.Executors

import pl.performance.Timer

import scala.concurrent.{Await, ExecutionContext, Future}
import scalaz.Scalaz._

object ShakaClient extends App with ClientTestScenario {
  import io.shaka.http.Http.http
  import io.shaka.http.Request.GET

//  implicit val ec = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
  implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(12))

  def response = http(GET("http://localhost:8080/count"))

  def makeRequest = Future {
    response.entityAsString
  }

  val name = "shaka async"
  runTest()
}
