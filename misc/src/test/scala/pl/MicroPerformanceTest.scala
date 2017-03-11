package pl

import pl.MicroPerformance.TestSpecimen

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class MicroPerformanceTest extends MyFunSpec {

  def f(): Unit = {
    val a = for {
      i <- Future {
        1
      }
      j <- Future.failed(new RuntimeException)
    } yield i

    Await.result(a, 1 second)
  }


  def g(): Unit = {
    val a = for {
      i <- Future {
        1
      }
      j <- Future {
        throw new RuntimeException
      }
    } yield i

    Await.result(a, 1 second)
  }

  test("run") {
    val r = new MicroPerformance(TestSpecimen("fail", f), TestSpecimen("throw", g)).test()
    println(r)
  }

}
