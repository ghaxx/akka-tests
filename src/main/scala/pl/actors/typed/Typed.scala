package pl.actors.typed

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

class DefaultTyped extends Typed {
  val name = Random.nextInt
  println("creating typed actor")

  def wait(duration: Int): Future[String] = Future {
    println(s"waiting in $name")
    Thread.sleep(duration)
    s"waited $duration"
  }
}
trait Typed {
  def wait(duration: Int): Future[String]
}
