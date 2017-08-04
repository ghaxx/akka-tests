package pl

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.jms.scaladsl.{JmsSink, JmsSource}
import akka.stream.alpakka.jms.{JmsSinkSettings, JmsSourceSettings}
import akka.stream.scaladsl.{Sink, Source}
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.broker.BrokerService

import scala.concurrent.Await

object StreamJms extends App {
  import scala.concurrent.duration._

  val broker = new BrokerService

  // configure the broker
  val url = "tcp://localhost:61616"
  broker.addConnector(url)
  broker.start()
  println("Broker started")

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val connectionFactory = new ActiveMQConnectionFactory(url)
  val jmsSource: Source[String, NotUsed] = JmsSource.textSource(
    JmsSourceSettings(connectionFactory).withBufferSize(10).withQueue("test")
  )
  val jmsSink: Sink[String, NotUsed] = JmsSink.textSink(
    JmsSinkSettings(connectionFactory).withQueue("test")
  )

  val in = List("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k")
  Source(in).runWith(jmsSink)

  Thread.sleep(10)

  val result = jmsSource
    .take(in.size)
      .map(x => x + "!")
    .runWith(Sink.seq)
  println(Await.result(result, 1 second))

  Thread.sleep(100)
}
