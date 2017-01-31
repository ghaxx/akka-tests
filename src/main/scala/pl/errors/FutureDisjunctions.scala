package pl.errors

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import scalaz.{\/, -\/, \/-}

object FutureDisjunctions extends App {

  def f(a: String): Future[Bad \/ Int] = Future {
    Some(a).map(_.toInt).fold(-\/(Bad("No number?")): Bad \/ Int) {
      i ⇒
        if (i < 0) -\/(Bad("Number should be greater than 0"))
        else \/-(i)
    }
  }

  f("a").onComplete {
    case Success(d) ⇒ println("""d = """ + d)
    case Failure(e) ⇒ println("""e = """ + e)
  }

  for {a ← f("1")} {
    println("""f("1") = """ + a)
  }

  for {a ← f("0")} {
    println("""f("0") = """ + a)
  }

  for {
    a ← f("1")
    b ← f("2")
  } {
    println("""1 + 2 = """ + a + b)
  }

  val r3 = for {
    a ← f("1")
    b ← f("b")
    c ← f("7")
  } yield {
    println("""1 + a + 7 = """ + a + b + c)
    List(a,  b, c)
  }

  for {
    a ← f("1")
    b ← f("-1")
  } {
    println("""1 + -1 = """ + a + b)
  }

//  println("""Await.result(r3, 5 seconds) = """ + Await.result(r3, 5 seconds))

  try {
    for {a ← f("")} {
      println("""f("") = """ + a)
    }
  } catch {
    case e: Throwable ⇒ println("""e.getMessage = """ + e.getMessage)
  }

  try {
    for {a ← f("a")} {
      println("""f("a") = """ + a)
    }
  } catch {
    case e: Throwable ⇒ println("""e.getMessage = """ + e.getMessage)
  }

  Thread.sleep(2000)
}
