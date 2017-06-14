package pl.inheritnce

object TestInheritance extends App {

  trait T {
    def f: Int
    def g: Int
  }

  class T1 extends T {
    override lazy val f = 1
    override lazy val g = f * 2
  }

  class T2 extends T1 {
    override lazy val f = 5
  }

  val t= new T2
  println(s"""t.g = ${t.g}""")
  println(s"""t.f = ${t.f}""")

}
