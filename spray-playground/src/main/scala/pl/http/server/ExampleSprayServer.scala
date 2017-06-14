package pl.http.server

import java.io.FileInputStream
import java.security.{KeyStore, SecureRandom}
import java.util.concurrent.atomic.AtomicLong
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

import akka.actor.{Actor, ActorContext, ActorLogging, ActorSystem, Props}
import akka.io.IO
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http
import spray.http.StatusCodes.InternalServerError
import spray.io.{ClientSSLEngineProvider, SSLContextProvider, ServerSSLEngineProvider}
import spray.routing.{ExceptionHandler, HttpService}
import spray.util.LoggingContext

import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.Random
import scala.util.control.NonFatal

object ExampleSprayServer extends App with LazyLogging {
//  implicit val system = ActorSystem("main-system", ConfigFactory.parseResources("akka-server.conf"))
  implicit val system = ActorSystem("main-system")
  implicit val timeout = Timeout(3 seconds)
  import SslFactory.serverEngineProvider

  IO(Http) ! Http.Bind(
    system.actorOf(Props(new Routes)), "localhost", 9080
  )

  logger.info("Server online at http://localhost:9080")
  logger.info("Press RETURN to stop")
  StdIn.readLine()
}
class Routes extends Actor with HttpService with ActorLogging {

  private implicit val exceptionHandler: ExceptionHandler = {
    ExceptionHandler {
      case NonFatal(e) =>
        ctx =>
        {
          log.warning(s"Warn: ${e.getMessage}")
          ctx.complete(InternalServerError)
        }
    }
  }

  def actorRefFactory: ActorContext = context

  def receive: Receive = runRoute(route)

  val route =
    get {
      path("random") {
        complete("" + Random.nextInt(100))
      }
    }
}

object SslFactory {
  private def context(): SSLContext = {
    val keyStoreResource = "_interim.jks"
    val password         = "abcdef"

    val keyStore = KeyStore.getInstance("jks")
    keyStore.load(new FileInputStream(keyStoreResource), password.toCharArray)

    val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
    keyManagerFactory.init(keyStore, password.toCharArray)

    val trustManagerFactory = TrustManagerFactory.getInstance("SunX509")
    trustManagerFactory.init(keyStore)

    val context = SSLContext.getInstance("TLS")
    context.init(keyManagerFactory.getKeyManagers, trustManagerFactory.getTrustManagers,
      new SecureRandom)
    context
  }

  implicit def clientEngineProvider =
    ClientSSLEngineProvider { engine =>
      engine.setEnabledCipherSuites(Array("TLS_RSA_WITH_AES_128_CBC_SHA"))
      engine.setEnabledProtocols(Array("SSLv3", "TLSv1"))
      engine.setUseClientMode(true)
      engine
    }(SSLContextProvider.forContext(context()))

  implicit def serverEngineProvider =
    ServerSSLEngineProvider { engine =>
      engine.setEnabledCipherSuites(Array("TLS_RSA_WITH_AES_128_CBC_SHA"))
      engine.setEnabledProtocols(Array("SSLv3", "TLSv1"))
      engine.setUseClientMode(false)
      engine.setNeedClientAuth(true)
      // engine.setWantClientAuth(true) // todo remove this
      engine
    }(SSLContextProvider.forContext(context()))
}
