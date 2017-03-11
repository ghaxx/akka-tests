package pl.scalaz.reader_monad

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scalaz._
import Scalaz._

object ReaderExample extends App {

  class Repo {
    private val storage = mutable.Map.empty[Int, String]
    def apply(id: Int) = Future successful storage(id)
    def update(id: Int, value: String) = Future successful (storage(id) = value)
  }

  val appender = Reader[String, String] {
    s ⇒ s + " from reader"
  }

  val counter = Reader[String, Int](
    _.length
  )

  def appenderF(s: String) = s + " from reader"
  def counterF(s: String) = s.length
  def lowerF(s: String) = s.toLowerCase()
  def upperF(s: String) = s.toUpperCase()

  val repo = new Repo

  val r = for {
    _ ← repo.update(1, "Terefere")
    stored ← repo.apply(1)
  } yield stored ▹ appenderF ▹ counterF

  val result = Await.result(r, 1 second)
  println("""result = """ + result)
}
