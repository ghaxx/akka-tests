package pl.http.clients

import java.util.concurrent.Executors

import pl.performance.Timer

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.Try
import scalaz.Scalaz._
import org.asynchttpclient._
import pl.http.client.Request

object AsyncHttpClient extends App with ClientTestScenario {


  import scala.concurrent.duration._
  import pl.http.client.ScalaAsyncHttpClient._

//  implicit val ec = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
//    implicit val ec = ExecutionContext.fromExecutorService(Executors.newWorkStealingPool())
//    implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))



  val cf = new DefaultAsyncHttpClientConfig.Builder()
    .setIoThreadsCount(2)
//    .setMaxConnections(1)
    .build()
  val asyncHttpClient = new DefaultAsyncHttpClient(cf)
  //  val asyncHttpClient = new DefaultAsyncHttpClient()

  val get1 = Request.Get("http://localhost:8080/count")

  val name = "async"
  def makeRequest: Future[String] = asyncHttpClient.asyncExecuteAsString(get1)

  runTest()
  asyncHttpClient.close()
}
