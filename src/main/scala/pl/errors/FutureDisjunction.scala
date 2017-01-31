package pl.errors

import scala.concurrent.{Promise, Future}
import scalaz.{-\/, \/, \/-}
import scala.concurrent.ExecutionContext.Implicits.global

object FutureDisjunction extends App {

  implicit def a[A, B](f: Future[A \/ B]): \?/[A, B] = \?/(f)

  val a: String \?/ Int = \?/(Future {\/-(1 / 0)})

  for {
    i ← a
    j ← a
  } {
    println(i)
  }

  Thread.sleep(1000)

}

case class \?/[+A, +B](f: Future[A \/ B]) {

  def flatMap[AA >: A, D](g: B ⇒ (AA \?/ D)): (AA \?/ D) = {
    val b = f map {
      case aa@ -\/(_) ⇒
        aa: (AA \/ D)
      case \/-(b) ⇒
        g(b)
    }
    b match {
      case f: Future[AA \/ D] ⇒ \?/(f)
      case x: (AA \?/ D) ⇒ x
    }
  }

  def map[D](g: B ⇒ D): (A \?/ D) = {
    val b = f map {
      case aa@ -\/(_) ⇒
        aa: (A \/ D)
      case \/-(b) ⇒
        g(b)
    }
    b match {
      case f: Future[A \/ D] ⇒ \?/(f)
      case x: D ⇒ \?/(Future successful \/-(x))
    }
  }

  def foreach[D](g: B ⇒ D): Unit = {
    val b = f map {
      case -\/(_) ⇒
      case \/-(b) ⇒ g(b)
    }
  }

}
