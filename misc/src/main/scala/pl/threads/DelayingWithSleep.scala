package pl.threads

import java.util.concurrent.Executors

import pl.performance.Timer

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.Try

object DelayingWithSleep extends App {
import scala.concurrent.duration._

  val e = Executors.newScheduledThreadPool(1)
//  val e = Executors.newSingleThreadExecutor()
//  val e = Executors.newWorkStealingPool()
  implicit val ec = ExecutionContext.fromExecutor(e)

  def d(dur: Int) = Future {
    Thread.sleep(dur)
  }

  def f(a: Int, i: Int, t: Timer) = Future {
    println(s"[${t.elapsed}] Sleeping $a: $i")
    Try(Await.ready(Promise().future, i.millis))
    println(s"[${t.elapsed}] Slept $a: $i")
    a
  }

  val t = Timer("time")
  val f1 = f(1, 500, t)
  val f2 = f(2, 500, t)
  val f3 = f(3, 500, t)
  val r = for {
    r1 <- f1
    r2 <- f2
    r3 <- f3
  } yield r1 + r2 + r3
  println(Await.result(r, Duration.Inf))
  println(t.status)
  e.shutdownNow()
}
