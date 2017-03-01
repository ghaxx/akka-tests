package pl.http.clients

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import org.asynchttpclient._


object ScalaAsyncHttpClient {
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
}