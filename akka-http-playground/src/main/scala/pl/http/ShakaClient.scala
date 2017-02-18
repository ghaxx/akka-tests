package pl.http

import java.util.concurrent.Executors

import pl.performance.Timer

import scala.concurrent.{Await, ExecutionContext, Future}
import scalaz.Scalaz._

object ShakaClient extends App {
  import io.shaka.http.Http.http
  import io.shaka.http.Request.GET
  import scala.concurrent.duration._

  implicit val ec = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
//  implicit val ec = ExecutionContext.fromExecutorService(Executors.newWorkStealingPool())

  def response = http(GET("http://localhost:8080/count"))

  def asyncResponse = Future {
    response.entityAsString
  }
//  val t1 = Timer("shaka sync")
//  val r1 = response
//  val r2 = response
//  val r3 = response
//  r1 |> println
//  r1 |> println
//  r1 |> println
//  println(t1.status)

  val t2 = Timer("shaka async")
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
