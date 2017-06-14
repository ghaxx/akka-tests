package pl.http.clients

import java.io.IOException

import okhttp3._

import scala.concurrent.{Future, Promise}

object OkHttpClient extends App with ClientTestScenario {

  implicit class ScalaAsyncHttpClient(val call: Call) extends AnyVal {
    def asyncExecute(): Future[Response] = {
      asyncExecuteAndMap(identity)
    }
    def asyncExecuteAsString(): Future[String] = {
      asyncExecuteAndMap(response => response.body().string())
    }
    private def asyncExecuteAndMap[T](mapper: Response => T): Future[T] = {
      val p = Promise[T]()
      call.enqueue(new Callback() {
        def onFailure(call: Call, e: IOException) = {
          p.failure(e)
        }

        def onResponse(call: Call, response: Response) = {
          p.success(mapper(response))
        }
      })
      p.future
    }
  }

  val client = new OkHttpClient()
  val request: Request = new Request.Builder()
    .url("http://localhost:8080/count")
    .build()
  def get1 = client.newCall(request)

  val name = "ok http"
  def makeRequest: Future[String] = get1.asyncExecuteAsString()
  runTest()

}
