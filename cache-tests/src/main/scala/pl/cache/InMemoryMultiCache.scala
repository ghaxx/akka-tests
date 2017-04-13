package pl.cache

import java.util.concurrent.atomic.AtomicInteger

import scala.collection.mutable

class InMemoryMultiCache(size: Long) extends MultiKeyCache[InMemoryMultiCache.Key, String]{
  private val writeLock = new Object
  private val cache = new InMemoryCache[InMemoryMultiCache.Key, String](size)
  private val accessLog = mutable.Queue.empty[InMemoryMultiCache.Key]

  def apply(key: InMemoryMultiCache.Key) = {
    cache(key)
  }

  def put(data: String)(implicit c: MultiKeyCache.Cacheable[InMemoryMultiCache.Key, String]) = {
    writeLock.synchronized {
      c.keys(data).foreach {
        key => cache(key) = data
      }
    }
  }
  def invalidate(data: String)(implicit c: MultiKeyCache.Cacheable[InMemoryMultiCache.Key, String]) = {
    writeLock.synchronized {
      c.keys(data).foreach {
        key => cache.invalidate(key)
      }
    }
  }
}

object InMemoryMultiCache {
  sealed trait Key
  case class IntKey(value: Int) extends Key

  implicit object MultiCacheable extends MultiKeyCache.Cacheable[Key, String] {
    var i = new AtomicInteger()
    def keys(data: String) = {
      List(IntKey(i.getAndIncrement()))
    }
  }
}
