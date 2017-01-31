package pl.misc

import org.scalatest.{FlatSpec, Matchers}

class CollectionAccessSpeedTest extends FlatSpec with Matchers {

  val tries = Int.MaxValue / 100000
  val list = (1 to tries) toList
  val vector = (1 to tries) toVector

  it should "measure access speed to first element of a vector" in {
    for (i <- 1 to tries) {
      vector.head shouldBe 1
    }
  }

  it should "measure access speed to last element of a vector" in {
    for (i <- 1 to tries) {
      vector.last shouldBe tries
    }
  }

  it should "measure access speed to first element of a list" in {
    for (i <- 1 to tries) {
      list.head shouldBe 1
    }
  }

  it should "measure access speed to last element of a list" in {
    for (i <- 1 to tries) {
      list.last shouldBe tries
    }
  }
}
