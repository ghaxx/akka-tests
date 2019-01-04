package pl.tracing

import cakesolutions.kafka.testkit.KafkaServer

object EmbeddedKafkaAndZookeeperFromCakeApp extends App {
  new KafkaServer(9092).startup()
}
