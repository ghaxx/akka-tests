package pl.tiles

/**
  * c4   c3
  *
  * c1   c2
  */

trait Tile {
  def c0: Int
  def c1: Int
  def c2: Int
  def c3: Int
  def rotatable: Boolean
  def rotate: Tile
  def apply(i: Int): Int
}

object Tile {
  def apply(c1: Int, c2: Int, c3: Int, c4: Int): Tile = RotatableTile(c1, c2, c3, c4, 0)
  val default = Tile(0, 1, 2, 3)
}

case class RotatableTile(c0: Int, c1: Int, c2: Int, c3: Int, state: Int) extends Tile {
  def rotate: Tile = {
    RotatableTile(c1, c2, c3, c0, state + 1)
  }
  def rotatable: Boolean = state < 3
  def apply(i: Int): Int = {
    i match {
      case 0 => c0
      case 1 => c1
      case 2 => c2
      case 3 => c3
    }
  }
}