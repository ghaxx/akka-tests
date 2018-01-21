package pl.http.clients

import java.io.{FileInputStream, InputStream}
import java.security.{KeyStore, SecureRandom}
import java.security.cert.X509Certificate
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory, X509TrustManager}

import akka.http.scaladsl.{ConnectionContext, HttpsConnectionContext}
import akka.stream.TLSClientAuth
import akka.util.ByteString
import com.typesafe.sslconfig.akka.AkkaSSLConfig

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object AkkaHttpsClient extends App with ClientTestScenario {
  import akka.actor.ActorSystem
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model._
  import akka.stream.ActorMaterializer

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
      implicit val executionContext = system.dispatcher
  implicit val timeout = 1 second
//  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())
//      implicit val executionContext = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(8))
//  val e = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(12))
//  val badSslConfig = AkkaSSLConfig().mapSettings(s => s.withLoose(s.loose.withAcceptAnyCertificate(true)))
//  val badCtx = Http().createClientHttpsContext(badSslConfig)
//  val customContext = HttpsConnectionContext(sc, sslParameters = Some(params))

  val password: Array[Char] = "qwe123".toCharArray // do not store passwords in code, read them from somewhere safe!

  val ks: KeyStore = KeyStore.getInstance("JKS")
  val keystore: InputStream = new FileInputStream("C:\\Users\\Kuba\\Development\\scala-experiments\\scala-tests\\akka-http-playground\\src\\main\\resources\\keystore-local.domain.jks")

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

  lazy val sslContext: SSLContext = SSLContext.getInstance("TLS")
  sslContext.init(keyManagerFactory.getKeyManagers, Array(tm), new SecureRandom)
  lazy val https: HttpsConnectionContext = ConnectionContext.https(
    sslContext = sslContext,
    clientAuth = Some(TLSClientAuth.want)
  )

    val badSslConfig = AkkaSSLConfig().mapSettings(s => s.withLoose(s.loose.withAcceptAnyCertificate(true)))
    val badCtx = Http().createClientHttpsContext(badSslConfig)

  Http().setDefaultClientHttpsContext(badCtx)
//  Http().setDefaultClientHttpsContext(https)

  def makeRequest: Future[String] = {
    Http()
      .singleRequest(HttpRequest(uri = "https://localhost:8080/random"))
      .flatMap {
        r =>
          r.entity.dataBytes
            .runFold(ByteString.empty) { case (acc, b) => acc ++ b }
            .map {
              x => x.utf8String
            }
      }
//      .flatMap { r =>
//        r.entity.toStrict(3 seconds)
//      }
//      .map(e => e.getData.decodeString("UTF-8"))
  }

  val name = "akka https async"
  /**
    * For 50 requests:
    * Exception in thread "main" akka.stream.BufferOverflowException: Exceeded configured max-open-requests value of [32]
    */
  runTest()
  Thread.sleep(10000)
  Await.result(system.terminate(), 10 seconds)

}
