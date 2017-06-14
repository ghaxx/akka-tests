trait T {
  def f: Int
}

class T1 extends T {
  def f = 1
}

class T2 extends T {
  val f = 2
  f + 8

}

class T3 extends T {
  val f = 3
}
