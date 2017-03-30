package pl.cache

import java.nio.ByteBuffer

import org.apache.commons.jcs.access.CacheAccess
import org.ehcache.config.units.MemoryUnit
import org.ehcache.spi.serialization.Serializer
import pl.cache.CacheBoot.cache
import pl.performance.Timer

object CacheBoot extends App {

  import org.ehcache.CacheManager
  import org.ehcache.config.builders.CacheConfigurationBuilder
  import org.ehcache.config.builders.CacheManagerBuilder
  import org.ehcache.config.builders.ResourcePoolsBuilder

  val config = CacheConfigurationBuilder
    .newCacheConfigurationBuilder(classOf[java.lang.Long], classOf[String], ResourcePoolsBuilder.heap(10))
//    .withKeySerializer(new Serializer[Long]{
//      def read(binary: ByteBuffer) = binary.getLong()
//      def serialize(`object`: Long) = ByteBuffer.wrap(`object`.toString.getBytes)
//      def equals(`object`: Long, binary: ByteBuffer) = `object` == binary.getLong()
//    })
//  .withSizeOfMaxObjectGraph(1)
//  .withSizeOfMaxObjectSize(1, MemoryUnit.B)


  val cacheManager = CacheManagerBuilder.newCacheManagerBuilder.withCache("preConfigured", config).build
  cacheManager.init()

  val preConfigured = cacheManager.getCache("preConfigured", classOf[java.lang.Long], classOf[String])

  val myCache = cacheManager.createCache("myCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(classOf[java.lang.Long], classOf[String], ResourcePoolsBuilder.heap(10)).build)

  val n = 3000000L
  val t = Timer("ehcache")
  println("Map")
  t.restart()
  val m = scala.collection.parallel.mutable.ParHashMap.empty[Long, String]
  (1L to n).foreach { i =>
    m.put(i, "terefere" + n)
    if (m.size > 10)
      m -=(i - 10)
  }
  println(t.elapsed)
  println("Ehcache")
  t.restart()
  (1L to n).foreach { i =>
    preConfigured.put(i, "terefere" + n)
  }
  println(t.elapsed)

  import org.apache.commons.jcs.JCS
  val cache: CacheAccess[java.lang.Long, java.lang.String] = JCS.getInstance("default")
  println("JCS")
  t.restart()
  (1L to n).foreach { i =>
    cache.put(i, "terefere" + n)
  }

  println("""cache.get(n) = """ + cache.get(n))
  println(t.elapsed)

  cacheManager.removeCache("preConfigured")

  cacheManager.close()
}
