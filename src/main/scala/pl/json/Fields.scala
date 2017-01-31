package pl.json

object Fields extends App {
  import org.json4s.{DefaultFormats, FieldSerializer}
  import org.json4s.native.Serialization

  implicit val formats = DefaultFormats
//  implicit val formats = DefaultFormats + new FieldSerializer[A]()

  class A(
           i: Int,
           /*val*/ j: Int
         ) {
//    val i2 = i
//    def i2 = i
  }

  println(Serialization.writePretty(new A(1, 2)))

}
