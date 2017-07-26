package pl.shapes

import pl.MyFunSpec
import shapeless.{::, HList, HNil}

import scala.concurrent.Future
// After https://gist.github.com/milessabin/6c9c060cf5159563b722d49ce9ee103e
class DynamicImplicitsTest extends MyFunSpec {

  import scala.concurrent.ExecutionContext.Implicits.global

  test("ev accepts outside options - not good") {

    class Ev {
      def apply[T](a: Future[T]): T = a.value.get.get
    }

    class Prep[A](val objects: List[Future[A]]) {
      def apply[T](a: Future[T]) = new Prep(a :: objects)
    }

    object Prep {
      def apply[T](a: Future[T]) = new Prep(a :: Nil)
    }

    implicit def prepToFuture[A](prep: Prep[A]): Future[Ev] = {
      Future.sequence(prep.objects).map(_ => new Ev)
    }

    val o1 = delayed(1)
    val o2 = delayed(2)
    val o3 = delayed(3)

    val result = for {
      x <- o1
      y = o2
      z = o3
      ev <- Prep(y)(z)
    } yield (x + ev(z)) * ev(Future.successful(7))

    result.futureValue shouldBe ((1 + 3) * 7)
  }

  test("ev accepts only computed options - good") {
    import Selector._
    class Ev[H <: HList] {
      def apply[T](a: Future[T])(implicit S: Selector[a.type, H]): T = a.value.get.get
    }

    class Prep[L <: HList](val objects: List[Future[Any]]) {
      def apply[T](a: Future[T]) = new Prep[a.type :: L](a :: objects)
    }

    object Prep {
      def apply[T](a: Future[T]) = new Prep[a.type :: HNil](a :: Nil)
    }

    implicit def prepToFuture[H <: HList](prep: Prep[H]): Future[Ev[H]] = {
      Future.sequence(prep.objects).map(_ => new Ev[H])
    }

    trait Selector[T, H <: HList]
    object Selector {
      // it works because it's looking for things matching return types
      implicit def inHead[H, T <: HList]: Selector[H, H :: T] = new Selector[H, H :: T] {}
      implicit def inTail[H, U, T <: HList](implicit S: Selector[U, T]): Selector[U, H :: T] = {
        new Selector[U, H :: T] {}
      }
    }


    val o1 = delayed(1)
    val o2 = delayed(2)
    val o3 = delayed(3)
    val o4 = delayed(4)

    val result = for {
      x <- o1
      y = o2
      z = o3
      z2 = o4
      ev <- Prep(y)(z)(z2)
    } yield (x + ev(z)) * ev(y) * ev(z2) //* ev(delayed(9)) // doesn't work

    result.futureValue shouldBe ((1 + 3) * 2 * 4)
  }

  def delayed[T](x: T): Future[T] = Future {
    Thread.sleep(100)
    x
  }
}
