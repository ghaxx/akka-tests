package pl.kafka_tests

import java.util.Properties

import akka.actor.ActorSystem
import akka.kafka.ProducerMessage.Message
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Attributes}
import akka.stream.scaladsl.{Sink, Source}
import akka.{Done, NotUsed}
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory

import scala.collection.immutable
import scala.concurrent.Future

object NumbersEmitter extends App {

  val logger = LoggerFactory.getLogger(getClass)

  implicit val system = ActorSystem("Producer")
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system).withInputBuffer(initialSize = 1, maxSize = 1))

  val config = system.settings.config.getConfig("kafka.producer")


  val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props) {
    override def send(record: ProducerRecord[String, String]) = {
      logger.debug(s"Producer.send ${record.value()}")
      super.send(record)
    }
    override def send(record: ProducerRecord[String, String], callback: Callback) = {
      logger.debug(s"Producer.send ${record.value()}")
      super.send(record, callback)
    }
  }


  private val producerSettings = ProducerSettings[String, String](config, new StringSerializer, new StringSerializer)
  private val sink: Sink[ProducerRecord[String, String], Future[Done]] = Producer.plainSink[String, String](producerSettings)
  private val flow = Producer.flow[String, String, NotUsed](producerSettings, producer)
  private val iterable: immutable.Iterable[ProducerRecord[String, String]] =
    Stream.from(0).map { x =>
      Thread.sleep(1000)
      logger.debug(s"Sending $x")
      new ProducerRecord[String, String]("numbers", x.toString)
    }

  Source(iterable)
    .map(Message(_, NotUsed))
    .via(flow)
    .mapAsync(1){x => Thread.sleep(1000); Future successful(x.message)}
    .runWith(new LoggingSink(logger))
//  Source(iterable).runWith(new LoggingSink(logger))

  //  private val producerSettings = ProducerSettings[String, String](config)
  //  private val sink: Sink[ProducerRecord[String, String], Future[Done]] = Producer.plainSink[String, String](producerSettings)
  //  private val iterable: immutable.Iterable[ProducerRecord[String, String]] =
  //    Stream.from(0).map { x =>
  //      Thread.sleep(1000)
  //      new ProducerRecord("numbers", (x % 6).toString, x.toString)
  //    }
  //
  //  Source(iterable).runWith(sink)
}
