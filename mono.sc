trait F[+A, -B] {
  def f[AA >: A, BB <: B](a: AA): BB
}
