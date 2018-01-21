package pl.tracing.kafka_tests

import akka.actor.ActorSystem
import akka.kafka.{ConsumerMessage, ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer.committableSource
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.typesafe.config.ConfigValueFactory
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.slf4j.LoggerFactory

import scala.concurrent.Future

object NumbersProcessor extends App {

  val logger = LoggerFactory.getLogger(getClass)

  implicit val system = ActorSystem("Processor")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val consumerConfig = system.settings.config.getConfig("kafka.consumer")
  private val consumerSettings = ConsumerSettings[String, String](consumerConfig, new StringDeserializer, new StringDeserializer)

  val producerConfig = system.settings.config.getConfig("kafka.producer")
  val producerSettings = ProducerSettings[String, String](producerConfig, new StringSerializer, new StringSerializer)
  val producer = Producer.flow[String, String, ConsumerMessage.CommittableOffset](producerSettings)

  committableSource(consumerSettings, Subscriptions.topics("numbers"))
    .mapAsync(1) {
      x =>
        val result = math.pow(x.record.value().toDouble, 2).toInt.toString
        logger.debug(s"Received message with offset: ${x.committableOffset} for ${x.record.value()}, sending $result")
        Future {
          ProducerMessage.Message(new ProducerRecord[String, String]("squares", result), x.committableOffset)
          throw new RuntimeException("Stop!")
        }
    }
    .via(producer)
    .mapAsync(1)(_.message.passThrough.commitScaladsl())
    .runWith(Sink.ignore)
}
