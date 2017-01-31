//package pl.kafka
//
//import akka.actor.ActorSystem
//import akka.stream.{ActorMaterializerSettings, ActorAttributes, Supervision, ActorMaterializer}
//import akka.stream.scaladsl.{Sink, Source}
//import com.softwaremill.react.kafka.KafkaMessages._
//import com.softwaremill.react.kafka._
//import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
//import org.reactivestreams.{Publisher, Subscriber}
//
//import scala.concurrent.{Await, Promise, Future}
//import scala.concurrent.duration._
//
//
//class HealthCheck9(sink: () => Sink[StringProducerMessage, Unit])(implicit val system: ActorSystem) {
//
//  def decider(p: Promise[String]): Supervision.Decider = {
//    case e: Throwable =>
//      println(e.getMessage)
//      p.failure(e)
//      Supervision.Stop
//
//    case x =>
//      p.success(x.toString)
//      println(x)
//      Supervision.Stop
//
//  }
////  implicit val materializer = ActorMaterializer()
//
//  val id = Iterator.continually(Stream.from(0)).flatten
//
//  def apply(): String = {
//    val p = Promise[String]
//    implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system).withSupervisionStrategy(decider(p)))
//    println("before sink")
//    Source.single("ping").map {
//      m =>
//        Thread.sleep(1000)
//        throw new RuntimeException("Got you!")
//        ValueProducerMessage(s"$m ${id.next()}")
//    }.runWith(sink().withAttributes(ActorAttributes.supervisionStrategy(decider(p))))
//    println("after sink")
////    Source.tick(1 second, 1 second, "ping").map { m => ValueProducerMessage(s"$m ${id.next()}") }.runWith(sink().withAttributes(ActorAttributes.supervisionStrategy(decider)))
//    try {
//      Await.result(p.future, 3 seconds)
//      "OK"
//    } catch {
//      case e: Throwable => "Wrong: " + e.getMessage
//    }
//  }
//}
