//package pl.kafka
//
//import java.util.Properties
//
//import akka.actor.ActorSystem
//import org.apache.kafka.clients.producer.{KafkaProducer, Callback, ProducerRecord, RecordMetadata}
//import org.apache.kafka.common.serialization.ByteArraySerializer
//
//import scala.concurrent.{ExecutionContext, Promise}
//
//class KafkaPingPublisher(host: String) {
//
//  val kafkaTopic = "test"
//  val message = "ping"
//  val bytes = message.getBytes
//
//  private val properties = new Properties()
//  properties.put("bootstrap.servers", host)
//  private val producer = new KafkaProducer(properties, new ByteArraySerializer, new ByteArraySerializer)
//
//  def ping(partitioningKey: Int)(implicit executionContext: ExecutionContext): Promise[Long] = {
//    val before = System.currentTimeMillis()
//    val producerRecord = new ProducerRecord(kafkaTopic, Array(partitioningKey.toByte), bytes)
//    val promise = Promise[Long]()
//    producer.send(producerRecord, new Callback() {
//      def onCompletion(metadata: RecordMetadata, e: Exception) {
//        val after = System.currentTimeMillis()
//        if (e != null) {
//          promise.failure(e)
//        } else {
//          promise.success(after - before)
//        }
//      }
//    })
//    promise
//  }
//}
