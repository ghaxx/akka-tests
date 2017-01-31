//package pl.kafka
//
//import com.softwaremill.react.kafka.ProducerMessage
//
//class KafkaActor {
//
//
//  import akka.actor.{Props, ActorRef, Actor, ActorSystem}
//  import akka.stream.ActorMaterializer
//  import org.apache.kafka.common.serialization.{StringSerializer, StringDeserializer}
//  import com.softwaremill.react.kafka.{ReactiveKafka, ProducerProperties, ConsumerProperties}
//
//  // inside an Actor:
//
//  implicit val system = ActorSystem.create("ReactiveKafka")
//  implicit val materializer = ActorMaterializer()
//  val kafka = new ReactiveKafka()
//
//  // subscriber
//  val producerProperties = ProducerProperties(
//    bootstrapServers = "ubuntu:9092",
//    topic = "test",
//    new StringSerializer()
//  )
//  val producerActorProps: Props = kafka.producerActorProps(producerProperties)
//  val subscriberActor: ActorRef = system.actorOf(producerActorProps)
//  // or:
//  val topLevelSubscriberActor: ActorRef = kafka.producerActor(producerProperties)
//
//  topLevelSubscriberActor ! ProducerMessage("ping actor")
//
//
//}
