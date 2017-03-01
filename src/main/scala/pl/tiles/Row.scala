package pl.tiles

case class Row(
  tiles: List[Tile]
) {
  def mutations: List[Row] = {
    def m(ts: List[Tile]): List[Row] = {
      ts match {
        case t :: tail if t.rotatable =>
          Row(t.rotate :: tail) :: m(tail).map {
            case Row(tiles) => Row(t :: tiles)
          }
        case _ => List.empty
      }
    }
    m(tiles)
  }
  def apply(i: Int) = tiles(i)
}

object Row {
  def default: Row = Row(List(Tile.default,Tile.default,Tile.default))
}