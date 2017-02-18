package pl.http.scalaj_client

import java.util.concurrent.Executors

import pl.performance.Timer

import scala.concurrent.{ExecutionContext, Future}
import scalaj.http._

object ScalajHttpClient extends App {
//  implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))
  implicit val ec = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
  val request: HttpRequest = Http("pl.http://localhost:8080/random")

  val t = Timer("time")
  def responseOne = Future {
    request.asString
  }
  def responseTwo = Future {
    request.asString
  }

  for {
    a <- responseOne
    b <- responseTwo
  } yield {
    println(s"${a.body}, ${b.body}")
    println(t.status)
    System.exit(0)
  }

  Thread.sleep(9000)
}
