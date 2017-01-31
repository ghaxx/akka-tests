package pl

trait MyLittleHelper {

  import MyLittleHelper._

  def logTime[T](description: String, printer: Any ⇒ Unit = println)(testedCode: ⇒ T): T = {
    val timer = Timer(description)
    val result = testedCode
    printer(timer.status)
    result
  }

  def time(description: String)(testedCode: ⇒ Any): Long = {
    val timer = Timer(description)
    testedCode
    timer.elapsed
  }

  def repeat[T](count: Long)(f: ⇒ T): Unit = {
    (1L to count).foreach(_ ⇒ f)
  }

  def repeatIdx[T](count: Long)(f: Long ⇒ T): Unit = {
    (1L to count).foreach(i ⇒ f(i))
  }

}

object MyLittleHelper {
  private class Timer(name: String) {
    private var time = System.currentTimeMillis()
    private val formatter = java.text.NumberFormat.getIntegerInstance
    def elapsed = System.currentTimeMillis() - time
    def status = s"[$name] - ${formatter.format(elapsed)}"
    def reset() = time = System.currentTimeMillis()
  }

  private object Timer {
    def apply(name: String) = new Timer(name)
  }
}