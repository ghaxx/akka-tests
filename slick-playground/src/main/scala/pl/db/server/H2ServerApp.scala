package pl.db.server

import java.lang.management.ManagementFactory

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.h2.tools.Server

object H2ServerApp extends App with LazyLogging {
  val config = ConfigFactory.load()
  val baseDir = config.getString("db.baseDir")
  logger.info(s"Base dir $baseDir")
  val port = config.getInt("db.port")
  logger.info(s"Port $port")

  val tcpArgs = s"-tcp -tcpAllowOthers -tcpPort $port -baseDir $baseDir".split(" ")


//  while(true) {
    println(System.currentTimeMillis() + " Starting " + ManagementFactory.getRuntimeMXBean().getName())
    try {
      val server = Server.createTcpServer(tcpArgs: _*).start()
//      (1 to 3) foreach { i =>
//        println(i)
//        Thread.sleep(1000)
//      }
//      println("Stopping")
//      server.stop()
//      Thread.sleep(100)
    } catch {
      case t: Throwable => println(t.getMessage)
    }
//  }
}
