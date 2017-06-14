package pl.http.clients

import java.util.concurrent.Executors

import org.apache.commons.io.IOUtils

import scala.concurrent.{ExecutionContext, Future, Promise}
import org.apache.http.HttpResponse
import org.apache.http.client.methods.{HttpGet, HttpRequestBase}
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.nio.client.HttpAsyncClient
import org.asynchttpclient.{Request, Response}
import pl.http.client.ScalaAsyncHttpClient.PromiseAsyncCompletionHandler;

object ApacheAsyncClient extends App with ClientTestScenario {

//  private val executorService = Executors.newSingleThreadExecutor()
//  implicit val ec = ExecutionContext.fromExecutorService(executorService)
//    implicit val ec = ExecutionContext.fromExecutorService(Executors.newWorkStealingPool())
//    implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))

  implicit class ScalaAsyncHttpClient(val httpClient: HttpAsyncClient) extends AnyVal {
    def asyncExecute(request: HttpRequestBase): Future[HttpResponse] = {
      asyncExecuteAndMap(request)(identity)
    }
    def asyncExecuteAsString(request: HttpRequestBase): Future[String] = {
      asyncExecuteAndMap(request)(r => IOUtils.toString(r.getEntity.getContent, "UTF-8"))
    }
    def asyncExecuteAndMap[T](request: HttpRequestBase)(mapper: HttpResponse => T): Future[T] = {
      val p = Promise[T]()
      httpClient.execute(request, new FutureCallback[HttpResponse]() {
        def completed(response: HttpResponse) {
          p.success(mapper(response))
        }
        def failed(ex: Exception) {
          p.failure(ex)
        }
        def cancelled() {
        }
      })
      p.future
    }
  }


  val httpClient = HttpAsyncClients.createDefault()
  httpClient.start()
  val get1 = new HttpGet("http://localhost:8080/count")

  val name = "async"
  def makeRequest: Future[String] = httpClient.asyncExecuteAsString(get1)
  runTest()
  httpClient.close()
}
