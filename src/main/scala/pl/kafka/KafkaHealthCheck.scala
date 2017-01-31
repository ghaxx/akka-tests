//package pl.kafka
//
//import akka.actor.ActorSystem
//
//import scala.concurrent._
//import scala.concurrent.duration.Duration
//
///**
//  * @param kafka
//  * @param timeout How long to wait for a broker to respond.
//  * @param numMessages Number of ping messages to sent. Should be equal or larger than the number of brokers.
//  *
//  * The "ping" topic must be partitioned so that messages are received by all brokers.
//  */
//class KafkaHealthCheck(host: String, timeout: Duration, numMessages: Int = 3) {
//  private val Kafka = "Kafka"
//  private val producer = new KafkaPingPublisher(host)
//
//  def apply()(implicit executionContext: ExecutionContext): String =
//    try {
//        val listOfFutures = (1 to numMessages) map { i => producer.ping(i).future }
//        val futureOfList = Future.sequence(listOfFutures)
//        val responseTimes = Await.result(futureOfList, timeout)
//        "OK"
//    } catch {
//      case e: Exception =>
//        "Wrong"
//    }
//
//}
