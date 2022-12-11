//package pl.threads
//
//import java.util.concurrent.Executors
//
//import pl.performance.Timer
//
//import scala.concurrent.{Await, ExecutionContext, Future, Promise}
//import scala.util.Try
//
//object DelayingWithExecutor extends App {
//import scala.concurrent.duration._
//  import scala.concurrent._
//  val e = Executors.newScheduledThreadPool(1)
////  val e = Executors.newWorkStealingPool()
//  implicit val ec = ExecutionContext.fromExecutor(e)
//
//  def d(dur: Int) = Future {
//    Thread.sleep(dur)
//  }
//
//  def f(a: Int, i: Int, t: Timer) = {
//    println(s"[${t.elapsed}] Sleeping $a: $i")
//    val p = Promise[Int]
//    e.schedule(new Runnable {
//      override def run() = {
//        p.success(a)
//        println(s"[${t.elapsed}] Slept $a: $i")
//      }
//    }, i, MILLISECONDS)
//    p.future
//  }
//
//  val t = Timer("time")
//  val f1 = f(1, 500, t)
//  val f2 = f(2, 500, t)
//  val f3 = f(3, 500, t)
//  val r = for {
//    r1 <- f1
//    r2 <- f2
//    r3 <- f3
//  } yield r1 + r2 + r3
//  println(Await.result(r, Duration.Inf))
//  println(t.status)
//  e.shutdownNow()
//}
