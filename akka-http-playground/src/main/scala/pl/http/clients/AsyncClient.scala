package pl.http.clients

import java.util.concurrent.Executors

import pl.performance.Timer

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.Try
import scalaz.Scalaz._

object AsyncClient extends App with ClientTestScenario {

  import org.asynchttpclient._

  import scala.concurrent.duration._
  import ScalaAsyncHttpClient._

//  implicit val ec = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
//    implicit val ec = ExecutionContext.fromExecutorService(Executors.newWorkStealingPool())
//    implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))



  val cf = new DefaultAsyncHttpClientConfig.Builder()
    .setIoThreadsCount(1)
    .build()
  val asyncHttpClient = new DefaultAsyncHttpClient(cf)
  //  val asyncHttpClient = new DefaultAsyncHttpClient()

  val get1 = asyncHttpClient.prepareGet("http://localhost:8080/count")

  val name = "async"
  def makeRequest: Future[String] = get1.asyncExecuteAsString()
  runTest()

}
