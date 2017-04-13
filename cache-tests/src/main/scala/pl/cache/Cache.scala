package pl.cache

import scala.collection.mutable

trait Cache[Key, Data] {

  import Cache._

  def apply(key: Key): CacheContent[Data]
  def update(key: Key, data: Data): Unit
  def invalidate(key: Key): Unit

}

object Cache {
  sealed trait CacheContent[+A] {
    def isEmpty: Boolean
    def get: A

    def map[B](f: A => B): CacheContent[B] =
      if (isEmpty) NoContent else Cached(f(this.get))

    def flatMap[B](f: A => CacheContent[B]): CacheContent[B] =
      if (isEmpty) NoContent else f(this.get)

    def filter(p: A => Boolean): CacheContent[A] =
      if (isEmpty || p(this.get)) this else NoContent
  }
  case object NoContent extends CacheContent[Nothing] {
    def isEmpty = true
    def get = throw new RuntimeException("Content not cached")
  }
  case class Cached[Data](data: Data) extends CacheContent[Data] {
    def isEmpty = false
    def get = data
  }
}