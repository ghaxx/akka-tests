package pl.http.server.streaming_tests

import java.util.concurrent.Executors

import org.asynchttpclient._
import pl.http.client.Request

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Random

object AsyncHttpClient extends App {
  import pl.http.client.ScalaAsyncHttpClient._
  import scala.concurrent.duration._

//  implicit val ec = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
    implicit val ec = ExecutionContext.fromExecutorService(Executors.newWorkStealingPool(500))
//    implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))



  val cf = new DefaultAsyncHttpClientConfig.Builder()
    .setIoThreadsCount(2)
//    .setMaxConnections(1)
    .build()
  val asyncHttpClient = new DefaultAsyncHttpClient(cf)
  //  val asyncHttpClient = new DefaultAsyncHttpClient()


  val name = "async"
  def makeRequest(text: String): Future[String] = asyncHttpClient.asyncExecuteAsString(Request.Get("http://localhost:8080/single/wait/$text"))

  val responses = (1000 to 100 by -100) map { i =>
      if (i % 1000 == 0) println(s"Made $i requests")
      val char = Random.nextPrintableChar()
    println(s"Sending $i")
    Thread.sleep(10)
      makeRequest("" + i) map {
        resp =>
        println(s"$char -> $resp")
      }
  }

//  Await.result(Future.sequence(responses), 1000 seconds)

  Thread.sleep(100000)
  asyncHttpClient.close()
}
