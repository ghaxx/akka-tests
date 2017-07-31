//package pl.kafka
//
//import java.text.NumberFormat
//import java.util.Locale
//
//import akka.actor.ActorSystem
//import akka.stream.scaladsl.Sink
//import com.softwaremill.react.kafka.KafkaMessages._
//import com.softwaremill.react.kafka.{ProducerProperties, ReactiveKafka}
//import kafka.serializer.StringEncoder
//import org.apache.kafka.common.serialization.StringSerializer
//import org.reactivestreams.Subscriber
//import scala.concurrent.duration._
//
//import scala.concurrent.Await
//
//object HealthCheckTest extends App {
//
//  implicit val actorSystem = ActorSystem("ReactiveKafka")
//  implicit val executionContext = actorSystem.dispatcher
//  val kafka = new ReactiveKafka()
//  def subscriber: Subscriber[StringProducerMessage] = kafka.publish(ProducerProperties(
//    bootstrapServers = "192.168.78.132:9092",
//    topic = "test",
//    valueSerializer = new StringSerializer()
//  ))
//
//  val check = new HealthCheck9(() => Sink.fromSubscriber(subscriber))
////  val check = new HealthCheck72(subscriber => Sink(subscriber))
//
//  while (true) {
//    println(check.apply())
//    Thread.sleep(1000)
//    System.gc()
//    val rt = Runtime.getRuntime
//    val usedMB = rt.totalMemory() - rt.freeMemory()
//    println("memory usage " + NumberFormat.getNumberInstance(Locale.GERMANY).format(usedMB))
//  }
//}
