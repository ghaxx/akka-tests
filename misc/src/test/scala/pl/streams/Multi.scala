package pl.streams

object Multi extends App {

  import akka.stream.ActorMaterializer
  import akka.NotUsed
  import akka.stream.scaladsl._
  import com.typesafe.config.ConfigFactory

  import scala.concurrent.Await
  import scala.concurrent.duration._

  implicit val system = akka.actor.ActorSystem("a")
  implicit val e = system.dispatcher
  implicit val m = ActorMaterializer()

  val s = Source(List(1, 2, 3))

  def f(s: Source[Int, NotUsed]) = s.runFold(0)(_ + _)
  def g(s: Source[Int, NotUsed]) = s.runFold(0)(_ + _)

  println(Await.result(f(s), 1 second))
  println(Await.result(g(s), 1 second))




}
