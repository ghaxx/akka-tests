package pl.streams.partitioned

import com.typesafe.scalalogging.LazyLogging


case class Msg(id: Int, group: Int, time: Int) extends LazyLogging {
  def sleepTime = 1 + time
  def key(max: Int) = {
    val _key = group % max
    logger.debug(s"Group for $this is ${_key}")
    //    Thread.sleep(10)
    _key
  }

  def work = {
    logger.debug(f"Working with $this")
    sleep()
    logger.debug(s"Done with $this")
    this
  }

  def sleep() = Thread.sleep(sleepTime)
}

case object Msg {
  def apply(id: Int) = new Msg(id, id, 100 * id)
  def apply(id: Int, time: Int) = new Msg(id, id, 100 * time)
}