package pl.performance

import pl.performance.Comparator._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.Try

class Comparator(f1: FunctionalUnit, f2: FunctionalUnit) {

  def run(strategy: ComparisonStrategy) = {
    warmup()
    val result = strategy.run(f1, f2)
    println(strategy.name)
    println(s"${result.f1.f.name} - ${result.f1.time}")
    println(s"${result.f2.f.name} - ${result.f2.time}")
  }

  private def warmup() = {
    val warmupTries = 100L
    repeatIdx(warmupTries) {
      i => f1.function(i)
    }
    repeatIdx(warmupTries) {
      i => f2.function(i)
    }
  }
}

object Comparator {
  case class FunctionalUnit(name: String, function: Long => Any)
  case class FunctionalUnitResult(f: FunctionalUnit, time: Long)
  case class ComparisonResult(f1: FunctionalUnitResult, f2: FunctionalUnitResult)

  sealed trait ComparisonStrategy {
    def name: String
    def run(f1: FunctionalUnit, f2: FunctionalUnit): ComparisonResult
  }
  object ComparisonStrategy {
    case class Sequential(tries: Long) extends ComparisonStrategy {
      def name: String = productPrefix
      def run(f1: FunctionalUnit, f2: FunctionalUnit): ComparisonResult = {
        val f1Result = time(f1.name) {
          repeatIdx(tries) {
            f1.function
          }
        }
        val f2Result = time(f2.name) {
          repeatIdx(tries) {
            f2.function
          }
        }
        ComparisonResult(
        FunctionalUnitResult(f1, f1Result),
        FunctionalUnitResult(f2, f2Result)
        )
      }
    }
    case class Mixing(tries: Long) extends ComparisonStrategy {
      def name: String = productPrefix
      def run(f1: FunctionalUnit, f2: FunctionalUnit): ComparisonResult = {
        val segments = 4
        val singleRunTries = tries / segments
        val t1 = Timer(f1.name)
        val t2 = Timer(f2.name)
        for (i <- 0 until segments) {
          t1.restart()
          repeatIdx(singleRunTries) {
            l => f1.function(l + singleRunTries * i)
          }
          t1.pause()

          t2.restart()
          repeatIdx(singleRunTries) {
            l => f1.function(l + singleRunTries * i)
          }
          t2.pause()
        }
        ComparisonResult(
          FunctionalUnitResult(f1, t1.elapsed),
          FunctionalUnitResult(f2, t2.elapsed)
        )
      }
    }
    case class RepetitionsInTime(duration: Duration) extends ComparisonStrategy {
      import scala.concurrent.ExecutionContext.Implicits.global
      def name: String = productPrefix
      def run(f1: FunctionalUnit, f2: FunctionalUnit): ComparisonResult = {
        var r1 = 0L
        var r2 = 0L
        val fut1 = Future {
          var i = 0L
          while(true) {
            f1.function(i)
            r1 += 1
            i += 1
          }
          i
        }
        Try(Await.result(fut1, duration)).getOrElse(0)

        val fut2 = Future {
          var i = 0L
          while(true) {
            f2.function(i)
            r2 += 1
            i += 1
          }
          i
        }
        Try(Await.result(fut2, duration)).getOrElse(0)

        ComparisonResult(
          FunctionalUnitResult(f1, r1),
          FunctionalUnitResult(f2, r2)
        )
      }
    }
  }

  def time(description: String)(testedCode: ⇒ Any): Long = {
    val timer = Timer(description)
    testedCode
    timer.elapsed
  }

  def repeatIdx[T](count: Long)(f: Long ⇒ T): Unit = {
    (1L to count).foreach(l ⇒ f(l))
  }
}