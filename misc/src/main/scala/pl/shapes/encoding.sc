import shapeless._

trait CsvEncoder[A] {
  def encode(a: A): List[String]
}

object CsvEncoder {
  def fromF[A](f: A => List[String]) = new CsvEncoder[A] {
    def encode(a: A) = f(a)
  }
}

implicit val intEnc: CsvEncoder[Int] = CsvEncoder.fromF(i => List(i.toString))
implicit val booleanEnc: CsvEncoder[Boolean] = CsvEncoder.fromF {
  case true => List("+")
  case false => List("-")
}
implicit val hnilEnc: CsvEncoder[HNil] = CsvEncoder.fromF(b => List.empty)
implicit def hlistEnc[H, T <: HList](
  implicit
  hEnc: Lazy[CsvEncoder[H]], // Lazy required for nested case classes and divergence errors
  tEnc: CsvEncoder[T]
): CsvEncoder[H :: T] = CsvEncoder.fromF { // two types needed to have the first one
  case h :: t =>
    hEnc.value.encode(h) ++ tEnc.encode(t)
}
implicit def genericEnc[A, L <: HList](
  implicit
  gen: Generic.Aux[A, L], // Generic that turns A into L: Generic[A] { type Repr = L }
  genEnc: CsvEncoder[L]
): CsvEncoder[A] = CsvEncoder.fromF(a => genEnc.encode(gen.to(a)))

def encodeCsv[A](a: A)(implicit enc: CsvEncoder[A]) = enc.encode(a)

case class C1(i: Int, b: Boolean)
case class C2(i: Int, b: Boolean, c1: C1)

println(encodeCsv(1))
println(encodeCsv(true))
println(encodeCsv(1 :: true :: HNil))
println(encodeCsv(C1(1, false)))
println(encodeCsv(C2(1, false, C1(2, true))))
