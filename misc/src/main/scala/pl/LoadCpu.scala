package pl

import java.util.concurrent.{ExecutorService, Executors}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

object LoadCpu extends App {

  implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2))

  val f = (1 to 2).map {
    i =>
//      Future {
        while(true) {
          val i = 13 * 99
        }
//      }
  }

//  Await.result(Future.sequence(f), 250 seconds)

}
