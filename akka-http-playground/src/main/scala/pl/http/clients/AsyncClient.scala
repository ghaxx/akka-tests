package pl.http.clients

import java.util.concurrent.Executors

import pl.performance.Timer

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.Try
import scalaz.Scalaz._

object AsyncClient extends App {

  import org.asynchttpclient._

  import scala.concurrent.duration._

  implicit val ec = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
//    implicit val ec = ExecutionContext.fromExecutorService(Executors.newWorkStealingPool())
//    implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))

  val cf = new DefaultAsyncHttpClientConfig.Builder()
    .setIoThreadsCount(2)
    .build()
  val asyncHttpClient = new DefaultAsyncHttpClient(cf)
//  val asyncHttpClient = new DefaultAsyncHttpClient()

  def asyncResponse: Future[String] = {
    val p = Promise[String]()
    asyncHttpClient.prepareGet("http://localhost:8080/count").execute(new AsyncCompletionHandler[Response]() {

      def onCompleted(response: Response): Response = {
        p.complete(Try(response.getResponseBody))
        response
      }

      override def onThrowable(t: Throwable) = {
        // Something wrong happened.
      }
    })
    p.future
  }

  val t2 = Timer("async")
  println("threads: " + Thread.activeCount())
  val fResponses = (1 to 30).map {
    _ => asyncResponse
  }
  println("threads: " + Thread.activeCount())
  fResponses.foreach {
    r => Await.result(r, 10 seconds) |> println
  }
  println("threads: " + Thread.activeCount())
  println(t2.status)


}
