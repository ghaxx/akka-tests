package pl.scalaz.disjunctions

import scala.util.Try
import scalaz.{Kleisli, \/-, -\/, \/}
import scalaz._
import scalaz.Monad._
import Scalaz._

object TestD extends App {
  type D[+T] = String \/ T

  val  a: String => String \/ Int = s => Try { \/-(s.toInt) } getOrElse -\/("not int")
  def b(s: Int): String \/ Double = if (s >= 0) \/-(math.sqrt(s)) else -\/("not sqrt")

  def a2: String => D[Int] = s => Try { \/-(s.toInt) } getOrElse -\/("not int")
  def b2: Int => D[Double] = s => if (s >= 0) \/-(math.sqrt(s)) else -\/("not sqrt")

//  val k = Kleisli.kleisliU[String, String \/ Int](a)// >=> Kleisli.kleisliU(b)
//
//  println("1: " + k("1"))
//  println("-1: " + k("-1"))
//  println("a: " + k("a"))

  val k = for {
    q <- a("1")
    w <- b(q)
  } yield w

  val l = Kleisli[D, String, Int](a2) >=> Kleisli[D, Int, Double](b2)

  println("l")
  println("1: " + l("1"))
  println("-1: " + l("-1"))
  println("a: " + l("a"))

}
