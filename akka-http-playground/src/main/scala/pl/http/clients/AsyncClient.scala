package pl.http.clients

import java.util.concurrent.Executors

import pl.performance.Timer

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.Try
import scalaz.Scalaz._

object AsyncClient extends App with ClientTestScenario {

  import org.asynchttpclient._

  import scala.concurrent.duration._

//  implicit val ec = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
//    implicit val ec = ExecutionContext.fromExecutorService(Executors.newWorkStealingPool())
//    implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))

  implicit class ScalaAsyncHttpClient(val request: BoundRequestBuilder) extends AnyVal {
    def asyncExecute(): Future[Response] = {
      asyncExecuteAndMap(identity)
    }
    def asyncExecuteAsString(): Future[String] = {
      asyncExecuteAndMap(response => response.getResponseBody)
    }
    private def asyncExecuteAndMap[T](mapper: Response => T): Future[T] = {
      val p = Promise[T]()
      request.execute(new AsyncCompletionHandler[Response]() {

        def onCompleted(response: Response): Response = {
          p.success(mapper(response))
          response
        }

        override def onThrowable(t: Throwable) = {
          p.failure(t)
        }
      })
      p.future
    }
  }

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
