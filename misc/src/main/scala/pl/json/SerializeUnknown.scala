package pl.json

object SerializeUnknown  extends App {
  import org.json4s.{DefaultFormats, FieldSerializer}
  import org.json4s.native.Serialization

  implicit val formats = DefaultFormats

  trait S
  case object S1 extends S
  case object S2 extends S
  case object S3 extends S

  trait T

  case class A(s: S, a1: Int, a2: Int) extends T
  case class B(s: S, b1: Int, b2: Int, b3: Int) extends T
  case class C(s: S, c1: Int, c2: Int, c3: Int, c4: Int) extends T

  Serialization.write(A(S1, 1, 1))


}
