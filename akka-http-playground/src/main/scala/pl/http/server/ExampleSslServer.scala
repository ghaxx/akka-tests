package pl.http.server

import java.io.{FileInputStream, InputStream}
import java.security.cert.X509Certificate
import java.security.{KeyStore, SecureRandom}
import java.util.concurrent.atomic.AtomicLong
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory, X509TrustManager}

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.{ConnectionContext, Http, HttpsConnectionContext}
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, TLSClientAuth}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.{Random, Try}

object ExampleSslServer extends App with LazyLogging with `TLS-Session-Info` {
  implicit val system = ActorSystem("main-system", ConfigFactory.parseResources("akka-server.conf"))
  implicit val timeout = Timeout(3 seconds)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val counter = new AtomicLong()

  private def delayedComplete[T](duration: Int)(x: => T)(implicit marshaller: ToResponseMarshaller[T]) =
    complete {
      Thread.sleep(duration)
      x
    }

  val port = Try(args(1).toInt).getOrElse(8080)

  private def requestDuration = 2000

  val dataStreaming = new DataStreaming

  val route =
    logRequestResult("Requests", Logging.InfoLevel) {
      path("random") {
        delayedComplete(requestDuration) {
          "" + Random.nextInt(100)
        }
      } ~ path("count") {
        delayedComplete(requestDuration) {
          "" + counter.getAndIncrement()
        }
      } ~ dataStreaming.route
    }

  val password: Array[Char] = "asdf!@#$".toCharArray // do not store passwords in code, read them from somewhere safe!

  val ks: KeyStore = KeyStore.getInstance("JKS")
//  val keystore: InputStream = getClass.getClassLoader.getResourceAsStream("gbl03817.keystore")
  val keystore: InputStream = new FileInputStream("C:\\sandbox\\dev\\scala-tests\\akka-http-playground\\src\\main\\resources\\gbl03817.keystore")

  require(keystore != null, "Keystore required!")
  ks.load(keystore, password)

  val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
  keyManagerFactory.init(ks, password)

  val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
  tmf.init(ks)
  val tm = new X509TrustManager {
    val i = tmf.getTrustManagers.apply(0).asInstanceOf[X509TrustManager]
    override def checkServerTrusted(x509Certificates: Array[X509Certificate], s: String) = {
      println("checkServerTrusted")
      println(s"""x509Certificates = ${x509Certificates}""")
      println(s"""s = ${s}""")
      i.checkServerTrusted(x509Certificates, s)
    }
    override def checkClientTrusted(x509Certificates: Array[X509Certificate], s: String) = {
      println("checkServerTrusted")
      println(s"""x509Certificates = ${x509Certificates}""")
      println(s"""s = ${s}""")
      i.checkClientTrusted(x509Certificates, s)
    }
    override def getAcceptedIssuers = {
      i.getAcceptedIssuers
    }
  }

  val sslContext: SSLContext = SSLContext.getInstance("TLS")
  sslContext.init(keyManagerFactory.getKeyManagers, Array(tm), new SecureRandom)
  val https: HttpsConnectionContext = ConnectionContext.https(
    sslContext = sslContext,
    clientAuth = Some(TLSClientAuth.want)
  )

  Http().setDefaultServerHttpContext(https)
  val bindingFuture = Http().bindAndHandle(route, Try(args(0)).getOrElse("localhost"), port)

  logger.info(s"Server online at $port")
  logger.info("Press RETURN to stop")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
