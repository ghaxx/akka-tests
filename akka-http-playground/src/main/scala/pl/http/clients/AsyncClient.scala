package pl.http.clients

import java.util.concurrent.Executors

import pl.performance.Timer

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.Try
import scalaz.Scalaz._

object AsyncClient extends App with ClientTestScenario {

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

  implicit class ScalaAsyncHttpClient(val request: BoundRequestBuilder) extends AnyVal {
    def asyncExecute(): Future[Response] = {
      val p = Promise[Response]()
      request.execute(new AsyncCompletionHandler[Response]() {

        def onCompleted(response: Response): Response = {
          p.success(response)
          response
        }

        override def onThrowable(t: Throwable) = {
          // Something wrong happened.
        }
      })
      p.future
    }
    def asyncExecuteAsString(): Future[String] = {
      asyncExecute().map(_.getResponseBody)
    }
  }

  val get1 = asyncHttpClient.prepareGet("http://localhost:8080/count")

  val name = "async"
  def makeRequest: Future[String] = get1.asyncExecuteAsString()
  runTest()

}
