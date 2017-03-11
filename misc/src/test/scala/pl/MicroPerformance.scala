package pl

import java.text.DecimalFormatSymbols

import pl.MicroPerformance._
import pl.performance.Timer

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class MicroPerformance(specimen1: TestSpecimen, specimen2: TestSpecimen, config: Config = DefaultConfig) {


  import scala.concurrent.ExecutionContext.Implicits.global

  def test(): Results = {
    val resultsFuture = for {
      _ <- warmup
      result1 <- runTest(specimen1)
      result2 <- runTest(specimen2)
    } yield Results(result1, result2)

    Await.result(resultsFuture, config.timeToSpare + (1 second))
  }

  private def warmup = Future {
    (1 to 4) foreach { _ =>
      try {
        specimen1.subject()
      } catch {
        case _: Throwable =>
      }
      try {
        specimen2.subject()
      } catch {
        case _: Throwable =>
      }
    }
  }

  private def runTest(specimen: TestSpecimen) = Future {
    val times = collection.mutable.ListBuffer.empty[Long]
    val globalTimer = Timer(specimen.name)
    val localTimer = Timer("local")
    while (globalTimer.elapsed <= config.timeToSpare.toNanos / 2) {
      localTimer.reset()
      try {
        specimen.subject()
      } catch {
        case _: Throwable =>
      }
      times += localTimer.elapsed
    }

    Result(specimen.name, times.toList)
  }
}

object MicroPerformance {
  private val numberFormatter = {
    val otherSymbols = new DecimalFormatSymbols()
    otherSymbols.setDecimalSeparator('.')
    otherSymbols.setGroupingSeparator(' ')
    val formatter = new java.text.DecimalFormat("", otherSymbols)
    formatter.setMaximumFractionDigits(0)
    formatter.setParseIntegerOnly(true)
    formatter
  }
  private val decimalFormatter = {
    val otherSymbols = new DecimalFormatSymbols()
    otherSymbols.setDecimalSeparator('.')
    otherSymbols.setGroupingSeparator(' ')
    val formatter = new java.text.DecimalFormat("", otherSymbols)
    formatter.setMaximumFractionDigits(3)
    formatter
  }

  case class TestSpecimen(name: String, subject: () => Unit)

  case class Results(result1: Result, result2: Result) {
    override def toString: String =
      s"""$result1
         |$result2
      """.stripMargin
  }

  case class Result(name: String, times: List[Long]) {
    lazy val total = times.sum
    lazy val numberOfTries = times.size
    lazy val mean = total.toDouble / numberOfTries
    lazy val variance = times.map(x => math.pow(x - mean, 2)).sum / numberOfTries
    override def toString: String =
      s"""Result of $name:
          | mean: ${decimalFormatter.format(mean)}
          | variance: ${decimalFormatter.format(variance)}
          | total: ${numberFormatter.format(total)}
          | numberOfTries: ${numberFormatter.format(numberOfTries)}
       """.stripMargin
  }

  case class Config(
    timeToSpare: Duration
  )

  val DefaultConfig = Config(2 seconds)

}
