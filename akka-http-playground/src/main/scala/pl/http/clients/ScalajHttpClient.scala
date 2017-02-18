package pl.http.clients

import java.util.concurrent.Executors

import pl.performance.Timer

import scala.concurrent.{Await, ExecutionContext, Future}
import scalaj.http._
import scalaz.Scalaz._
import scala.concurrent.duration._

object ScalajHttpClient extends App {
//  implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))
  implicit val ec = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
  val request: HttpRequest = Http("http://localhost:8080/random")

  def asyncResponse = Future {
    request.asString.body
  }

  val t = Timer("scalaj async")
  println("threads: " + Thread.activeCount())
  val fResponses = (1 to 30).map {
    _ => asyncResponse
  }
  println("threads: " + Thread.activeCount())
  fResponses.foreach {
    r => Await.result(r, 10 seconds) |> println
  }
  println("threads: " + Thread.activeCount())
  println(t.status)
}
