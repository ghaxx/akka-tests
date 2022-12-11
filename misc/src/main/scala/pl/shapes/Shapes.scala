package pl.shapes

import shapeless._
import shapeless.ops.hlist.Reverse

object Shapes extends App {

  import ops.function._
  import syntax.std.function._

  val f = (s: String, i: Int) => s"$s $i"
  val ft: Tuple2[String, Int] => String = f.tupled

  val fp = FnToProduct[(String, Int) => String]

  //  val fhl: String::Int::HNil => String = fp.apply(f) // or, equivalently, fp(f)

  //  val fh2: String::Int::HNil => String = f.toProduct
  def applyProduct[P <: Product, F, L <: HList, R](p: P)(f: F)
    (implicit gen: Generic.Aux[P, L], fp: FnToProduct.Aux[F, L => R]) =
    f.toProduct(gen.to(p))


  case class Summer[Types <: HList] private(sources: List[Option[_]]) {

    def withOption[T](o: Option[T]) = Summer[T :: Types](o :: sources)

    def apply[F, R <: HList, Out](f: F)(
      implicit fp: FnToProduct.Aux[F, R => Out], rev: Reverse.Aux[Types, R]
    ): Out = {
      val a: Types = sources.foldLeft[HList](HNil)((m, v) ⇒ v.get :: m).asInstanceOf[Types]
      f.toProduct(rev(a))
    }
  }

  object Summer {
    def withOption[T](o: Option[T]) = Summer[T :: HNil](o :: Nil)
  }

  //    Summer[Int :: Int :: HNil](Some(1) :: Some(2) :: Nil).apply {
  //      (x: Int, y: Int) ⇒ println((x, y))
  //    }

  val r = Summer
    .withOption(Some(1))
    .withOption(Some(2))
    .withOption(Some(3))
    .apply {
      (x: Int, y: Int, z: Int) ⇒ println(s"$x + $y + $z")
    }

  println(r)
}
