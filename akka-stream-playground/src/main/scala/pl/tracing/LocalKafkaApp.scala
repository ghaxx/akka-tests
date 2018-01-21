package pl.tracing

object LocalKafkaApp extends App {
  new EmbeddedKafkaAndZookeeper().start()
}
