package pl.cache.testing

import com.github.blemale.scaffeine.{ Cache, Scaffeine }
import scala.concurrent.duration._
import pl.performance.Timer

class ScaffeineTestCase extends TestCase {
  val cache: Cache[Long, String] =
    Scaffeine()
      .recordStats()
      .expireAfterWrite(1.hour)
      .maximumSize(1000)
      .build[Long, String]()

  def name = "Scaffeine"
  def warmup() = {
    (1L to 100).foreach { i =>
      cache.put(i, "terefere" + i)
    }
  }

  def run(n: Long) = {
    val t = Timer("map")
    var i = 0L
    while(i < n) {
      cache.put(i, "terefere" + i)
      i += 1
    }
    cache.getIfPresent()
    t.elapsed
  }
}
