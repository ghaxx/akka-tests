package pl.tiles

object Printer {

  def draw(w: Wall): Unit = {
    println("---+---+---")
    for {
      row <- w.rows
    } {
      println(s"${row(0)(3)} ${row(0)(2)}|${row(1)(3)} ${row(1)(2)}|${row(2)(3)} ${row(2)(2)}")
      println(s"${row(0)(0)} ${row(0)(1)}|${row(1)(0)} ${row(1)(1)}|${row(2)(0)} ${row(2)(1)}")
      println("---+---+---")
    }
  }

}
