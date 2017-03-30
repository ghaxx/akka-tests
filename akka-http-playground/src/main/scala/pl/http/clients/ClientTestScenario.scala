package pl.http.clients

import pl.performance.Timer

import scala.concurrent.{Await, ExecutionContext, Future}

trait ClientTestScenario {
  import scala.concurrent.duration._
  import scalaz.Scalaz._

  val name: String

  def makeRequest: Future[String]

  def runTest(numberOfRequests: Int = 32) = {
    val timer = Timer(name)
    println("Threads before requests: " + Thread.activeCount())
    val fResponses = (1 to numberOfRequests).map {
      _ => makeRequest
    }
    println("Threads after requests: " + Thread.activeCount())
    fResponses.foreach {
      r => Await.result(r, 10 seconds) |> println
    }
//    Await.result(Future.sequence(fResponses), 60 seconds).foreach(println)
    println("Threads in the end: " + Thread.activeCount())
    println(timer.status)
  }

}
