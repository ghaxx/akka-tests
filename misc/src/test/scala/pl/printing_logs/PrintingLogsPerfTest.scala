package pl.printing_logs

import org.scalatest.FunSuite
import pl.MyLittleHelper
import pl.performance.Comparator
import pl.performance.Comparator.FunctionalUnit

class PrintingLogsPerfTest extends FunSuite with MyLittleHelper {
  import scala.concurrent.duration._

  val tries = 15358945L

  test("Compare") {
    val c = new Comparator(
      FunctionalUnit("Separately", { i =>
        p("a1" + i + i)
        p("b1" + i + i)
        p("c1" + i + i)
      }),
      FunctionalUnit("Together", { i =>
        val a = "a2" + i + i
        val b = "b2" + i + i
        val c = "c2" + i + i
        p(a + b + c)
      })
    )
    c.run(Comparator.ComparisonStrategy.Mixing(tries))
    c.run(Comparator.ComparisonStrategy.Sequential(tries))
    c.run(Comparator.ComparisonStrategy.RepetitionsInTime(2 seconds))
  }

  test("printing logs separately") {
    logTime("separately") {
      repeatIdx(tries) {
        i =>
          p("a1" + i + i)
          p("b1" + i + i)
          p("c1" + i + i)
      }
    }
  }

  test("printing logs together") {
    logTime("together") {
      repeatIdx(tries) {
        i =>
          val a = "a2" + i + i
          val b = "b2" + i + i
          val c = "c2" + i + i
          p(a + b + c)
      }
    }
  }


  test("printing logs separately 2") {
    logTime("separately") {
      repeatIdx(tries) {
        i =>
          p("a3" + i + i)
          p("b3" + i + i)
          p("c3" + i + i)
      }
    }
  }

  test("printing logs together 2") {
    logTime("together") {
      repeatIdx(tries) {
        i =>
          val a = "a4" + i + i
          val b = "b4" + i + i
          val c = "c4" + i + i
          p(a + b + c)
      }
    }
  }

  def p(s: String) = {

  }
}
