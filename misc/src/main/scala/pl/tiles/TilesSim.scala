package pl.tiles

object TilesSim extends App {


  val firstWall = Wall.default

  def c(w: Wall): List[(Wall, Int)] = {
    (w, w.score) :: w.mutations.flatMap(c)
  }


  c(firstWall).foreach {
    case (w, s) =>
      Printer.draw(w)
  }

}
