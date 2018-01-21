package pl.threads_memory

import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.Executors

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

object TestThreadsSlidingMemory extends App with LazyLogging {

  implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(100))
  MyLittleHelper.time("exec") {
    (1 to 100).sliding(10).foreach {
      x =>
        val f = Future {
          logger.info("x = " + x)
          val a = Array.fill(x(0) * 1000000)(1.toByte)
          Thread.sleep(500)
          mem()
          a
        }
    }
  }

  def mem() = {
    System.gc()
    val rt = Runtime.getRuntime
    val usedMB = rt.totalMemory() - rt.freeMemory()
    logger.info("Memory usage " + NumberFormat.getNumberInstance(Locale.GERMANY).format(usedMB))
  }


  object MyLittleHelper {

    def logTime[T](description: String, printer: Any ⇒ Unit = println)(testedCode: ⇒ T): T = {
      val timer = Timer(description)
      val result = testedCode
      printer(timer.status)
      result
    }

    def time(description: String)(testedCode: ⇒ Any): Long = {
      val timer = Timer(description)
      testedCode
      timer.elapsed
    }

    def repeat[T](count: Long)(f: ⇒ T): Unit = {
      (1L to count).foreach(_ ⇒ f)
    }

    def repeatIdx[T](count: Long)(f: Long ⇒ T): Unit = {
      (1L to count).foreach(i ⇒ f(i))
    }

    private class Timer(name: String) {
      private var time = System.currentTimeMillis()
      private val formatter = java.text.NumberFormat.getIntegerInstance
      def elapsed = System.currentTimeMillis() - time
      def status = s"[$name] - ${formatter.format(elapsed)}"
      def reset() = time = System.currentTimeMillis()
    }

    private object Timer {
      def apply(name: String) = new Timer(name)
    }
  }
}
