package pl.cache.testing

import java.lang

import org.ehcache.CacheManager
import org.ehcache.config.builders.{CacheConfigurationBuilder, CacheManagerBuilder, ResourcePoolsBuilder}
import pl.performance.Timer

class EhcacheTestCase extends TestCase {
  var myCache: org.ehcache.Cache[java.lang.Long, String] = _
  var cacheManager: CacheManager = _
  def name = "Ehcache"
  def warmup() = {

    val config = CacheConfigurationBuilder
      .newCacheConfigurationBuilder(classOf[java.lang.Long], classOf[String], ResourcePoolsBuilder.heap(10))
    //    .withKeySerializer(new Serializer[Long]{
    //      def read(binary: ByteBuffer) = binary.getLong()
    //      def serialize(`object`: Long) = ByteBuffer.wrap(`object`.toString.getBytes)
    //      def equals(`object`: Long, binary: ByteBuffer) = `object` == binary.getLong()
    //    })
    //  .withSizeOfMaxObjectGraph(1)
    //  .withSizeOfMaxObjectSize(1, MemoryUnit.B)


    cacheManager = CacheManagerBuilder.newCacheManagerBuilder.withCache("preConfigured", config).build
    cacheManager.init()

//    preConfigured = cacheManager.getCache("preConfigured", classOf[java.lang.Long], classOf[String])

    val cacheConfiguration = CacheConfigurationBuilder
      .newCacheConfigurationBuilder(classOf[lang.Long], classOf[String], ResourcePoolsBuilder.heap(1000))
      .build
    myCache = cacheManager.createCache("myCache", cacheConfiguration)
    (1L to 100).foreach { i =>
      myCache.put(i, "terefere" + i)
    }
  }

  def run(n: Long) = {
    val t = Timer("ehcache")
    var i = 0L
    while(i < n) {
      myCache.put(i, "terefere" + i)
      i += 1
    }
    t.stop()

//    cacheManager.removeCache("preConfigured")

//    cacheManager.close()

    t.elapsed
  }
}
