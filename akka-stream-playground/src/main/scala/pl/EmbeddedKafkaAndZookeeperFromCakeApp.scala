package pl

import cakesolutions.kafka.testkit.KafkaServer
import kafka.server.{KafkaConfig, KafkaServerStartable}
import org.apache.curator.test.TestingServer

import scala.collection.JavaConverters._

object EmbeddedKafkaAndZookeeperFromCakeApp extends App {
  new KafkaServer(9092).startup()
}
