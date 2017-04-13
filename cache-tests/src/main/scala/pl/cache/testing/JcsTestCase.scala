package pl.cache.testing

import org.apache.commons.jcs.access.CacheAccess
import pl.performance.Timer

class JcsTestCase extends TestCase {
  var cache: CacheAccess[java.lang.Long, java.lang.String] = _
  def name = "JCS"
  def warmup() = {
    import org.apache.commons.jcs.JCS
    cache = JCS.getInstance("default")
    (1L to 100).foreach { i =>
      cache.put(i, "terefere" + i)
    }
  }

  def run(n: Long) = {
    val t = Timer("JCS")
    var i = 0L
    while(i < n) {
      cache.put(i, "terefere" + n)
      i += 1
    }
    t.elapsed
  }
}
