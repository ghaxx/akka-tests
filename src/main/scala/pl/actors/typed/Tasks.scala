package pl.actors.typed

import scalaz.Scalaz._
import scalaz._
import scalaz.concurrent.Task
import scalaz.concurrent.Task._

object Tasks extends App {

  val s1 = Task({
      Thread.sleep(500)
      println("Executing s1")
      1
    })
  val s2 = Task {
    Thread.sleep(300)
    println("Executing s2")
    2
  }
  val f1 = Task {
    Thread.sleep(500)
    println("Executing f1")
    throw new scala.RuntimeException("Failure #1")
  }
  val f2 = Task {
    Thread.sleep(300)
    println("Executing f2")
    throw new scala.RuntimeException("Failure #2")
  }
  val w1 = Task {
    Thread.sleep(500)
    println("Executing f1")
    -\/(new scala.RuntimeException("Failure #1"))
  }
  val w2 = Task {
    Thread.sleep(300)
    println("Executing f2")
    -\/(new scala.RuntimeException("Failure #2"))
  }

  fullSuccess()
//  fullFailure()
//  fullFailureWithDisjunction()

  def fullSuccess(): Unit = {
    println("entering full success loop")
    val r = for {
      r1 <- s1
      _ = println("s1...")
      r2 <- s2
      _ = println("... and s2")
    } yield {
      println(s"r1 = $r1")
      println(s"r2 = $r2")
//      (r1.validation.toValidationNel |@| r2.validation.toValidationNel) tupled
    }
    println("after for")

    println(r.unsafePerformSync)
  }

  def fullFailure(): Unit = {
    println("entering full failure loop")
    val f3 = Nondeterminism[Task].gatherUnordered(Seq(f1.attempt, f2.attempt))
    val r = for {
//      r1 <- suspend(f1.attempt)
//      _ = println("f1...")
//      r2 <- f2.attempt
//      _ = println("... and f2")
      r3 <- f3
    } yield {
        val List(r1, r2) = r3
      println(s"r1 = $r1")
      println(s"r2 = $r2")
      (r1.validation.toValidationNel |@| r2.validation.toValidationNel) tupled
    }
    println("after for")

    println(r.unsafePerformSync)
  }

  def fullFailureWithDisjunction(): Unit = {
    println("entering full failure loop")
    val r = for {
      r1 <- w1
      _ = println("w1...")
      r2 <- w2
      _ = println("... and w2")
    } yield {
      println(s"r1 = $r1")
      println(s"r2 = $r2")
      (r1.validation.toValidationNel |@| r2.validation.toValidationNel) tupled
    }
    println("after for")

    println(r.unsafePerformSync)
  }
}
