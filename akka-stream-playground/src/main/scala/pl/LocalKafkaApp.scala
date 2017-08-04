package pl

object LocalKafkaApp extends App {
  new EmbeddedKafkaAndZookeeper().start()
}
