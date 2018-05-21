package pl.split_graph

import org.slf4j.LoggerFactory

class Processor(
  private var _state: Int
) {

  val logger = LoggerFactory.getLogger("println")

  def state = _state
  def state_=(x: Int) = {
    Thread.sleep(2000)
    logger.info(s"Updated state: $state -> $x")
    _state = x
  }

  def process(x: Int): Int = {
    Thread.sleep(500)
    val newState = x + state
    logger.info(s"Processing result with state $state: $newState")
    newState
  }
}
