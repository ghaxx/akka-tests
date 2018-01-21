package pl.tracing.kafka_tests

import java.util.{Collections, Properties}

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

object ClassicKafkaProducer extends App {
  val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  var x = 0
  while(true) {
    Thread.sleep(1000)
    println(s"Sending $x")
    producer.send(new ProducerRecord[String, String]("numbers", x.toString))
    x = -(~x) // increment
  }



}
