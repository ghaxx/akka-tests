package pl.threads

import java.util.concurrent.{ForkJoinPool, Semaphore, TimeUnit}

import scala.concurrent.{ExecutionContext, Future}

object LockingThreadsOneObj extends App {
  //implicit val ec = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(2))
  implicit val ec = ExecutionContext.fromExecutor(new ForkJoinPool(16))
  val ec2 = ExecutionContext.fromExecutor(new ForkJoinPool(4))
  val ec3 = ExecutionContext.fromExecutor(new ForkJoinPool(4))

  (1 to 1).foreach { i =>
    external(i)
  }

  Thread.sleep(15000)

  def external(i: Int)(implicit ec: ExecutionContext): Future[Int] = {
    println(s"${i}: External")
    for {
      a <- Future(i)
      _ = println(s"${a}: First value")
      b <- throttled(a)(ec2)
    } yield b
  }

  val throttle = new Semaphore(3, true)
  def throttled(i: Int)(implicit ec: ExecutionContext): Future[Int] = {
    println(s"${i}: Acquiring lock")
    throttle.tryAcquire(Long.MaxValue, TimeUnit.SECONDS)
    println(s"${i}: Inside throttled area")
    val result = Future {
      Thread.sleep(100)
      println(s"${i}: Doing task")
      i + 1
    }
    println(s"${i}: Task initialized")
    result.onComplete { r =>
      println(s"${i}: Releasing lock")
      throttle.release()
    }(ec3)
    result
  }

}
