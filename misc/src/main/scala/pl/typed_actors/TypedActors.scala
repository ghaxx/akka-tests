//package pl.typed_actors
//
//import java.util.concurrent.TimeUnit
//
//import akka.typed._
//import akka.typed.ScalaDSL._
//import akka.typed.AskPattern._
//import akka.util.Timeout
//import scala.concurrent.Future
//import scala.concurrent.duration._
//import scala.concurrent.Await
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//
//object TypedActors extends App {
//
//  final case class Greet(whom: String, replyTo: ActorRef[Greeted])
//
//  final case class Greeted(whom: String)
//
//  implicit val timeout = Timeout(5, TimeUnit.SECONDS)
//
//  val greeter = Static[Greet] {
//    msg ⇒
//      println(s"Hello ${msg.whom}!")
//      msg.replyTo ! Greeted(msg.whom)
//  }
//
//  val system: ActorSystem[Greet] = ActorSystem("hello", Props(greeter))
//  val future: Future[Greeted] = system ? (Greet("world", _))
//
//  for {
//    greeting ← future.recover { case ex => ex.getMessage }
//    done ← { println(s"result: $greeting"); system.terminate() }
//  } println("system terminated")
//
//}
