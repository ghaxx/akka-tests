package pl.kafka_tests

import akka.actor.ActorSystem
import akka.kafka.{ConsumerMessage, ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer.committableSource
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.slf4j.LoggerFactory
import pl.kafka_tests.NumbersProcessor.getClass

object SquaresConsumer extends App {


  val logger = LoggerFactory.getLogger(getClass)

  implicit val system = ActorSystem("SquaresConsumer")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val consumerConfig = system.settings.config.getConfig("kafka.consumer")
  private val consumerSettings = ConsumerSettings[String, String](consumerConfig, new StringDeserializer, new StringDeserializer)

  committableSource(consumerSettings, Subscriptions.topics("squares"))
      .mapAsync(1) {
        message =>
          message.committableOffset.commitScaladsl().map {
            _ => message.record
          }
      }
    .runWith(new LoggingSink(logger))
}
