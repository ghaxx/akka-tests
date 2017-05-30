package pl.cache.testing

import pl.performance.Timer

class ParMapTestCase extends TestCase {
  var m = scala.collection.parallel.mutable.ParHashMap.empty[Long, String]
  def name = "ParHashMap"
  def warmup() = {
    m = scala.collection.parallel.mutable.ParHashMap.empty[Long, String]
    (1L to 100).foreach { i =>
      m.put(i, "terefere" + i)
      if (m.size > 1000)
        m -= (i - 10)
    }
  }

  def run(n: Long) = {
    val t = Timer("map")
    var i = 0L
    while(i < n) {
      m.put(i, "terefere" + i)
      if (m.size > 1000)
        m -= (i - 10)
      i += 1
    }
    t.elapsed
  }
}
