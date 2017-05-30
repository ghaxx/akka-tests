package pl.cache.testing

import pl.cache.InMemoryMultiCache
import pl.performance.Timer

class InMemoryCacheTestCase extends TestCase {
  import InMemoryMultiCache._
  var m = new InMemoryMultiCache(1000)
  def name = "Own implementation"
  def warmup() = {
    (1L to 100).foreach { i =>
      m.put("terefere" + i)
    }
  }

  def run(n: Long) = {
    val t = Timer("map")
    var i = 0L
    while(i < n) {
      m.put("terefere" + i)
      i += 1
    }
    t.elapsed
  }
}
