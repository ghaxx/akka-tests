package pl.cache

trait MultiKeyCache[Key, Data]  {
  def apply(key: Key)
  def put(data: Data)(implicit c: MultiKeyCache.Cacheable[Key, Data])
  def invalidate(data: Data)(implicit c: MultiKeyCache.Cacheable[Key, Data])
}

object MultiKeyCache {

  trait Cacheable[+Key, -Data] {
    def keys(data: Data): Seq[Key]
  }

}