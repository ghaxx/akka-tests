//package pl.kafka
//
//import akka.actor.ActorSystem
//import akka.stream.ActorMaterializer
//import akka.stream.scaladsl.{Source, Sink}
//import com.softwaremill.react.kafka.{ProducerProperties, ReactiveKafka}
//import kafka.serializer.StringEncoder
//import org.reactivestreams.Subscriber
//
//
//class HealthCheck72(sink: Subscriber[String] => Sink[String, Unit]) {
//
//  implicit val actorSystem = ActorSystem("ReactiveKafka")
//  implicit val executionContext = actorSystem.dispatcher
//
//  val kafka = new ReactiveKafka()
//  implicit val materializer = ActorMaterializer()
//  val subscriber: Subscriber[String] = kafka.publish(ProducerProperties(
//    topic = "test", brokerList = "192.168.78.132:9092", encoder = new StringEncoder(),
//    clientId = "test")
//  )
//
//  def apply() = {
//    Source.single("ping").runWith(sink(subscriber))
//  }
//}
