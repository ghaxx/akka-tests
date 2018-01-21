package pl.tracing

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.jms.scaladsl.{JmsSink, JmsSource}
import akka.stream.alpakka.jms.{JmsSinkSettings, JmsSourceSettings}
import akka.stream.scaladsl.{Sink, Source}
import org.apache.activemq.broker.BrokerService
import org.apache.activemq.{ActiveMQConnection, ActiveMQConnectionFactory}

object StreamFromJmsToKafka extends App {

  val broker = new BrokerService
broker.setPersistent(false)
  // configure the broker
  val url = "vm://localhost?broker.persistent=false"
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


  val result = jmsSource
//    .take(1)
    .map(x => x + "!")
      .map(x => println ("""x = """ + x))
    .runWith(Sink.ignore)
//  println(Await.result(result, 10 seconds))
//  result.foreach{
//    r => println("""r = """ + r)
//  }

Thread.sleep(15000)
  val connection = connectionFactory.createConnection()
  connection.start()
  val session = connection.createSession(false, 1)
  val queue = session.createQueue("test")
  val producer = session.createProducer(queue)
  (1 to 10).foreach{
    i =>
      println("sending")
      producer.send(queue, session.createTextMessage("" + i))
      Thread.sleep(100)
  }

  import javax.jms.JMSException

  val ds = connection.asInstanceOf[ActiveMQConnection].getDestinationSource
  val queues = ds.getQueues

  import scala.collection.JavaConversions._

  for (queue <- queues) {
    try
      System.out.println(queue.getQueueName)
    catch {
      case e: JMSException =>
        e.printStackTrace()
    }
  }
  session.close()
  connection.close()

  val in = List("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k")
//  Source(in).runWith(jmsSink)

  Thread.sleep(1000)


  Thread.sleep(100)

  broker.stop()
}
