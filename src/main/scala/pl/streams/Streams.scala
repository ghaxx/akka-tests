//package pl.streams
//
//import akka.actor.ActorSystem
//import akka.stream.ActorMaterializer
//import akka.stream.scaladsl.Source
//
//import scala.concurrent.Future
//import scala.concurrent._
//import akka._
//import akka.actor._
//import akka.stream._
//import akka.stream.scaladsl._
//import akka.util._
//
//object Streams extends App {
//
//  implicit val system = ActorSystem("TestSystem")
//  implicit val materializer = ActorMaterializer()
//  import system.dispatcher
//
//  val s = Source(1 to 3)
//  println("s = " + s)
//  val s1 = Source.fromFuture(Future("single value from a Future"))
//  println("s1 = " + s1)
//
//  s runForeach println
//  s1 runForeach println
//
//  Iterator.continually(1 :: 2 :: 3 :: Nil).flatten
//
//  def run(actor: ActorRef) = {
//    Future { Thread.sleep(400); actor ! "x" }
//    Future { Thread.sleep(300); actor ! 1 }
//    Future { Thread.sleep(200); actor ! 2 }
//    Future { Thread.sleep(100); actor ! 3 }
//  }
//  val s2 = Source
//    .actorRef[Int](bufferSize = 0, OverflowStrategy.fail)
//    .mapMaterializedValue(run)
//  s2 runForeach println
//  val sink = Sink.foreach[Any](elem => println(s"sink received: $elem"))
//  val flow = s2 to sink
//  flow.run()
//}
