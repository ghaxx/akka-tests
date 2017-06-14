import scala.annotation.tailrec

object OOM extends App {

  import scala.concurrent._
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits._

  val arraySize = 1000000 // Pick a large number
  val tooManyArrays = (Runtime.getRuntime().totalMemory() / arraySize).toInt * 100 // Make sure that we will hit OOME if Promise Linking doesn't work

  // An example of possible space leak using ACPS by virtue of `recoverWith`:
  def loopRW(i: Int, arraySize: Int): Future[Unit] = {
    val array = new Array[Byte](arraySize)
    Future(throw new Exception).recoverWith { case _ =>
      if (i == 0) {
        Future(())
      } else {
        array.size // Force closure to refer to array
        loopRW(i - 1, arraySize)
      }
    }
  }

  Await.result(loopRW(tooManyArrays, arraySize), 30 seconds)

}
