package pl.split_graph

import scala.concurrent.duration.FiniteDuration

object DelayingStream {
  implicit class DelayingStream[T](stream: Stream[T]) {
    def delayElements(x: FiniteDuration): Stream[T] = {
      stream.map {
        e =>
          Thread.sleep(x.toMillis)
          e
      }
    }
  }
}
