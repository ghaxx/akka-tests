package pl.scalaz.disjunctions

import scalaz._
import Scalaz._

object Multiple extends App {

  trait Error

  case class E1(i: Int) extends Error

  case class E2(i: Int) extends Error

  case class E3(i: Int) extends Error

  case class D(i: Int)

  def f: E1 \/ E2 \/ E3 \/ D = D(7).right
  def g: E1 \/ E2 \/ E3 \/ D = E1(7).left.left.left
  def h: E1 \/ E2 \/ E3 \/ D = E2(7).right.left.left
  def i: E1 \/ E2 \/ E3 \/ D = E3(7).right.left

  f match {
    case r @ \/-(_) ⇒
    case l @ -\/(v) ⇒ v match {
      case r @ \/-(_) ⇒
      case l @ -\/(_) ⇒
    }
  }

  println("""f = """ + f)


}
