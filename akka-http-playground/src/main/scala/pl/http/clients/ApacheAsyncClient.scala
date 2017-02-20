package pl.http.clients

import java.util.concurrent.Executors

import org.apache.commons.io.IOUtils

import scala.concurrent.{ExecutionContext, Future, Promise}
import org.apache.http.HttpResponse
import org.apache.http.client.methods.{HttpGet, HttpRequestBase}
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.nio.client.HttpAsyncClient;

object ApacheAsyncClient extends App with ClientTestScenario {

  implicit val ec = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
//    implicit val ec = ExecutionContext.fromExecutorService(Executors.newWorkStealingPool())
//    implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))

  implicit class ScalaAsyncHttpClient(val request: HttpRequestBase) extends AnyVal {
    def asyncExecute(): Future[HttpResponse] = {
      val p = Promise[HttpResponse]()
      httpclient.execute(request, new FutureCallback[HttpResponse]() {
        def completed(response: HttpResponse) {
          p.success(response)
        }
        def failed(ex: Exception) {
          p.failure(ex)
        }
        def cancelled() {
        }
      })
      p.future
    }
    def asyncExecuteAsString(): Future[String] = {
      asyncExecute().map(r => IOUtils.toString(r.getEntity.getContent, "UTF-8"))
    }
  }


  val httpclient = HttpAsyncClients.createDefault()
  httpclient.start()
  val get1 = new HttpGet("http://localhost:8080/count")

  val name = "async"
  def makeRequest: Future[String] = get1.asyncExecuteAsString()
  runTest()

}
