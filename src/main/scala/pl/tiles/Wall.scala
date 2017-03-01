package pl.tiles

case class Wall(
  rows: List[Row]
) {
  def score: Int = 0
  def mutations: List[Wall] = {
    def m(rs: List[Row]): List[Wall] = {
      rs match {
        case r :: tail =>
          val l1 = r.mutations.map(r => Wall(r :: tail))
          val l2 = m(tail).map(t => Wall(r :: t.rows))
          l1 ::: l2
        case Nil => List.empty
      }
    }
    m(rows)
  }
}

object Wall {
  val default: Wall = {
    def rowGen: Stream[Row] = Stream(Row.default) #::: rowGen
    Wall(rowGen.take(4).toList)
  }
}