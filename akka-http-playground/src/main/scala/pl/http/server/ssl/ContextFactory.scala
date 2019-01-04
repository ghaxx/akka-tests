package pl.http.server.ssl

import java.io.{FileInputStream, InputStream}
import java.security.{KeyStore, SecureRandom}
import java.security.cert.X509Certificate

import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory, X509TrustManager}

object ContextFactory {

  def createSslCtx = {
    val password: Array[Char] = "qwe123".toCharArray // do not store passwords in code, read them from somewhere safe!

    val ks: KeyStore = KeyStore.getInstance("JKS")
    //  val keystore: InputStream = getClass.getClassLoader.getResourceAsStream("gbl03817.keystore")
    //  val keystore: InputStream = new FileInputStream("C:\\Users\\Kuba\\Development\\scala-experiments\\scala-tests\\akka-http-playground\\src\\main\\resources\\keystore-local.domain.jks")
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

    val sslContext: SSLContext = SSLContext.getInstance("TLS")
    sslContext.init(keyManagerFactory.getKeyManagers, Array(tm), new SecureRandom)
    sslContext
  }
}
