package pl.actors.typed

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FutSeq extends App {
  val list = for {
    a <- List(1, 2, 3)
  } yield Future {
    a
  }

  println(list)

  for {
    r <- Future.sequence(list)
  } yield {
    println(r)
    r
  }

  Thread.sleep(100)
}
