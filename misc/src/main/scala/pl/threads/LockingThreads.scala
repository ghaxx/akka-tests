package pl.threads

import java.util.concurrent.{ForkJoinPool, Semaphore}

import pl.threads.LockingThreads.log

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

object LockingThreads extends App {
  val ec1 = ExecutionContext.fromExecutor(new ForkJoinPool(8))
  val ec2 = ExecutionContext.fromExecutor(new ForkJoinPool(8))
  val ec3 = ExecutionContext.fromExecutor(new ForkJoinPool(8))
  implicit val ec = ExecutionContext.fromExecutor(new ForkJoinPool(1))
  //implicit val ec = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(2))

  val s3 = new S3()(ec1)
  val s2 = new S2Throttled(s3)(ec2)
  val s1 = new S1(s2)(ec1)

  var i = 0
  while (true) {
    s1.externalFunc(i)
    Thread.sleep(2)
    i += 1
  }

  while (true) {
    Thread.sleep(10000)
  }

  def log(thread: Thread, s: String) = synchronized {
    println(s"${thread.getName} -> $s")
  }
}


class S1(s2: S2)(implicit ec: ExecutionContext) {
  val throttle = new Semaphore(2)
  def externalFunc(i: Int): Future[Int] = {
    //    l(Thread.currentThread, s"${i}: External")
    for {
      a <- Future(i)
      _ = log(Thread.currentThread, s"S1: ${a}: First value")
      b <- s2.throttled(a)
    } yield {

      b
    }
  }
}

trait S2 {
  def throttled(i: Int): Future[Int]
}

class S2Router(s3: S3)(implicit val ec2: ExecutionContext) extends S2 {
  val max = 2
  val workers = (1 to max).map { _ =>
    new S2Impl(s3)
  }
  val iterator = Iterator.continually(workers).flatten

  val throttle = new Semaphore(max)
  def throttled(i: Int): Future[Int] = {
    //    throttle.acquire()
    val worker = //iterator.synchronized(
      iterator.next()
    // )
    val r = worker.throttled(i)
    //    throttle.release()
    r
  }
}

class S2Impl(s3: S3)(implicit val ec2: ExecutionContext) extends S2 {

  //    val ec2 = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(2) )
  val throttle = new Semaphore(2)
  //  def throttled(i: Int)(implicit ec: ExecutionContext): Future[Int] = {
  def throttled(i: Int): Future[Int] = synchronized {
    log(Thread.currentThread, s"S2: ${i}: Acquiring lock")
    //    throttle.acquire()
    log(Thread.currentThread, s"S2: ${i}: Inside throttled area")
    val result = Future {
      Thread.sleep(77)
      log(Thread.currentThread, s"S2: ${i}: Doing task")
      i
    }.flatMap(s3.c)
    Thread.sleep(Random.nextInt(50) + 40)
    log(Thread.currentThread, s"S2: ${i}: Task initialized")
    result.onComplete { r =>
      log(Thread.currentThread, s"S2: ${i}: Releasing lock")
      //      throttle.release()
    }
    result
  }
}

class S2Throttled(s3: S3)(implicit val ec2: ExecutionContext) extends S2 {
  //  implicit val ec2 = ExecutionContext.fromExecutor(new ForkJoinPool(1))
  //  implicit val ec2 = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(2) )
  val throttle = new Semaphore(2)
  //  def throttled(i: Int)(implicit ec: ExecutionContext): Future[Int] = {
  def throttled(i: Int): Future[Int] = {
    log(Thread.currentThread, s"S2: ${i}: Acquiring lock")
    throttle.acquire()
    log(Thread.currentThread, s"S2: ${i}: Inside throttled area")
    val result = Future {
      Thread.sleep(77)
      log(Thread.currentThread, s"S2: ${i}: Doing task")
      i
    }.flatMap(s3.c)
    Thread.sleep(Random.nextInt(50) + 40)
    log(Thread.currentThread, s"S2: ${i}: Task initialized")
    result.onComplete { r =>
      log(Thread.currentThread, s"S2: ${i}: Releasing lock")
      throttle.release()
    }
    result
  }
}

class S2Throttled2(s3: S3)(implicit val ec2: ExecutionContext) extends S2 {
//  val ec3 = ExecutionContext.fromExecutor(new ForkJoinPool(2))
  //  implicit val ec2 = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(2) )
  val throttle = new Semaphore(2)
  //  def throttled(i: Int)(implicit ec: ExecutionContext): Future[Int] = {
  def throttled(i: Int): Future[Int] = {
    log(Thread.currentThread, s"S2: ${i}: Acquiring lock")
    throttle.acquire()
    Thread.sleep(100)
    Future(1)
    Thread.sleep(100)
    Future {
      log(Thread.currentThread, s"S2: ${i}: Inside throttled area")
      val result = Future {
        Thread.sleep(77)
        log(Thread.currentThread, s"S2: ${i}: Doing task")
        i
      }
      .flatMap(s3.c)
      Thread.sleep(Random.nextInt(50) + 40)
      log(Thread.currentThread, s"S2: ${i}: Task initialized")
      result.onComplete { r =>
        log(Thread.currentThread, s"S2: ${i}: Releasing lock")
        throttle.release()
      }
      result
    }.flatten
  }
}

class S2ThrottledWithSepEc(s3: S3)(implicit val ec2: ExecutionContext) extends S2 {
  val ec3 = ExecutionContext.fromExecutor(new ForkJoinPool(2))
  //  implicit val ec2 = ExecutionContext.fromExecutor(new ForkJoinPool(1))
  //  implicit val ec2 = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(2) )
  val throttle = new Semaphore(2)
  //  def throttled(i: Int)(implicit ec: ExecutionContext): Future[Int] = {
  def throttled(i: Int): Future[Int] = {
    log(Thread.currentThread, s"S2: ${i}: Acquiring lock")
    throttle.tryAcquire()
    log(Thread.currentThread, s"S2: ${i}: Inside throttled area")
    val result = Future {
      Thread.sleep(77)
      log(Thread.currentThread, s"S2: ${i}: Doing task")
      i
    }(ec3).flatMap(s3.c)(ec3)
    Thread.sleep(Random.nextInt(50) + 40)
    log(Thread.currentThread, s"S2: ${i}: Task initialized")
    result.onComplete { r =>
      log(Thread.currentThread, s"S2: ${i}: Releasing lock")
      throttle.release()
    }(ec3)
    result
  }
}

class S3(implicit val ec2: ExecutionContext) {

  def c(i: Int): Future[Int] = Future {
    Thread.sleep(31)
    log(Thread.currentThread, s"S3: $i: c()")
    i
  }
}