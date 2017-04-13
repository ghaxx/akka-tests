package pl.cache

import scala.collection.mutable

class InMemoryCache[Key, Data](size: Long) extends Cache[Key, Data] {
  private val cache = mutable.HashMap.empty[Key, Data]
  private val accessLog = mutable.Queue.empty[Key]

  def apply(key: Key) = {
    accessLog.enqueue(key)
    cache.get(key) match {
      case Some(value) => Cache.Cached(value)
      case None => Cache.NoContent
    }
  }

  def update(key: Key, data: Data) = {
    accessLog.synchronized {
      accessLog.enqueue(key)
      cache(key) = data
      while(cache.size > size) {
        cache.remove(accessLog.dequeue())
      }
    }
  }
  def invalidate(key: Key) = {
    accessLog.synchronized {
      accessLog.dequeueFirst(_ == key)
      cache.remove(key)
    }
  }
}
