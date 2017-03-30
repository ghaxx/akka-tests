package pl.performance

class Timer(name: String) {
  private val formatter = java.text.NumberFormat.getIntegerInstance
  private var currentElapsed = 0L
  private var running = true
  private var startTime = now
  def status = s"[$name] $prettyElapsed ms"
  def elapsed = {
    if (running) {
      currentElapsed += now - startTime
      startTime = now
    }
    currentElapsed
  }
  def prettyElapsed = {
    formatter.format(elapsed)
  }

  def restart() = {
    running = true
    currentElapsed = 0
    startTime = now
  }

  def stop() = {
    running = false
  }

  def pause() = {
    if (running) {
      currentElapsed += now - startTime
      running = false
    }
  }

  def start() = {
    running = true
    startTime = now
  }

  def measure(f: => Any): Long = {
    start()
    f
    pause()
    elapsed
  }

  private def now = System.currentTimeMillis()
}

object Timer {
  def apply(name: String) = new Timer(name)
  def stopped(name: String) = {
    val t = new Timer(name)
    t.stop()
    t.restart()
    t
  }
  def measure(f: => Any): Long = {
    val t = new Timer("")
    f
    t.elapsed
  }
  def measureAndReturn[T](f: => T): (Long, T) = {
    val t = new Timer("")
    val r = f
    (t.elapsed, r)
  }
}
