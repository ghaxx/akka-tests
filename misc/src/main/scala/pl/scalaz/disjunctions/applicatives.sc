import shapeless._
import shapeless.ops.hlist.Prepend

import scalaz.Scalaz._
import scalaz._
import scala.util.control.NonFatal
import scala.reflect.ClassTag


object Test {
  def meh[M[_], A](x: M[A]): M[A] = x
  meh{(x: Int) => x} // should solve ?M = [X] X => X and ?A = Int ...
}

case class EX[T](e: HList, x: T) {
  (1 :: 2 :: HNil) ::: (1 :: 2 :: HNil)
  def ap[B](f: => EX[(T) => B]): EX[B] =
    new EX(e ::: f.e, f.x(this.x))
  def map[B](f: (T) => B): EX[B] = new EX(e, f(x))
}

//implicit val aae = new Applicative[E] {
//  def point[A](a: => A): E[A] = E(List(a))
//  def ap[A, B](fa: => E[A])(f: => E[(A) => B]): E[B] =
//    E(fa.e.zip(f.e).map { case (v, f) ⇒ f(v) })
//
//}

object EX {
  implicit def ae: Apply[EX] = new Apply[EX] {
    def ap[A, B](fa: => EX[A])(f: => EX[(A) => B]): EX[B] = fa ap f
    def map[A, B](fa: EX[A])(f: (A) => B): EX[B] = fa map f
  }
}

import EX._

//implicitly[NonEmptyList[_] <:< Apply[NonEmptyList]]

val a = EX(6 :: 3 :: HNil, 12)
val b = EX(14 :: 8 :: 9 :: HNil, 8)


(b ⊛ a ⊛ a).tupled

val c = NonEmptyList(4, 5)
val d = NonEmptyList(6, 7)

c append (d)

implicitly[Apply[EX]]
//implicitly[Apply[Failure]]

(c.failure ⊛ d.failure).tupled
