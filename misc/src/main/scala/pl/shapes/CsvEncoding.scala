package pl.shapes

object CsvEncoding extends App {

  trait CsvEncoder[A] {
    def encode(a: A): List[String]
  }

  object CsvEncoder {
    def fromF[A](f: A => List[String]) = new CsvEncoder[A] {
      def encode(a: A) = f(a)
    }
  }

  implicit val intEnc: CsvEncoder[Int] = CsvEncoder.fromF(i => List(i.toString))


  println(1)

}
