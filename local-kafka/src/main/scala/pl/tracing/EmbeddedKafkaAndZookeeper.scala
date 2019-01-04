package pl.tracing

import java.nio.file.{Files, Paths}

import kafka.server.{KafkaConfig, KafkaServerStartable}
import org.apache.commons.io.FileUtils
import org.apache.curator.test.TestingServer

import scala.collection.JavaConverters._

class EmbeddedKafkaAndZookeeper {

  def start() = {
    new TestingServer(2181, true)

    FileUtils.deleteDirectory(Paths.get("logs").toFile)

    //    val kafkaProps = getClass.getResourceAsStream("kafka.properties")
    //    val props = new Properties().load(kafkaProps)

    val kafkaConfig = new KafkaConfig(Map(
      "broker.id" -> 1,
      "log.dirs" -> "logs",
      "port" -> "9092",
      "zookeeper.connect" -> "localhost:2181",
      "offsets.topic.replication.factor" -> 1.toShort,
      "log.flush.interval.messages" -> 1,
      "log.flush.interval.ms" -> 1000
    ).asJava)

    val kafka = new KafkaServerStartable(kafkaConfig)
    kafka.startup
  }
}
