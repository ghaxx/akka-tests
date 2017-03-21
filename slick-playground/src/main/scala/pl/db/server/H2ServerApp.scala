package pl.db.server

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
  val server = Server.createTcpServer(tcpArgs: _*).start()
}
