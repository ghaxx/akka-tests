package pl.print_source

import pl.MySpec

class ToSourceTest extends MySpec {

  import ToSource._

  it should "work" in {
    val t = LevelOne("n", 1.2, Map("k1" → "v1", "k2" → "v2"), Map("l1" → LevelTwo("n.n", 3, Map(), List("a", "b", "c"), Nil)))
    println(toSource(t))

    pl.print_source.LevelOne("n", 1.2, Map("k1" -> "v1", "k2" -> "v2"), Map("l1" -> pl.print_source.LevelTwo("n.n", 3.0, Map(), List("a", "b", "c"), List())))
  }

}


case class LevelOne(
                     name: String,
                     number: Double,
                     things: Map[String, String],
                     otherThings: Map[String, LevelTwo]
                   )

case class LevelTwo(
                     name: String,
                     number: Double,
                     things: Map[String, String],
                     list: List[String],
                     otherList: List[String]
                   )