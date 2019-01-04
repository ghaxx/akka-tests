package pl.http.server

import java.io.{FileInputStream, InputStream}
import java.security.cert.X509Certificate
import java.security.{KeyStore, SecureRandom}
import java.util.concurrent.atomic.AtomicLong

import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory, X509TrustManager}
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.impl.util.JavaAccessors
import akka.http.scaladsl.{ConnectionContext, Http, HttpsConnectionContext}
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model.{DateTime, HttpResponse}
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, TLSClientAuth}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.http.server.ResourcesSslServer.args
import pl.http.server.ssl.ContextFactory

import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.{Random, Try}

object ExampleSslServer extends App with LazyLogging {
  implicit val system = ActorSystem("main-system", ConfigFactory.parseResources("akka-server.conf"))
  implicit val timeout = Timeout(3 seconds)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val counter = new AtomicLong()

  val port = Try(args(1).toInt).getOrElse(8080)
  val host = Try(args(0)).getOrElse("a.localhost.local")

  def h = List(
    HttpCookie("data1", "values" + counter.get(), expires = Some(DateTime.now.plus(500000)), secure = true, domain = Some("localhost.local"), httpOnly = true),
    HttpCookie("data", "values" + counter.get(), expires = Some(DateTime.now.plus(500000)), secure = true, domain = Some("localhost.local")),
    HttpCookie("insecure", "values" + counter.get(), expires = Some(DateTime.now.plus(500000)), domain = Some("localhost.local"))
  )

  private def delayedComplete[T](duration: Int)(x: => T)(implicit marshaller: ToResponseMarshaller[T]) =
    complete {
      Thread.sleep(duration)
      HttpResponse(headers = h.map(`Set-Cookie`(_)))
      x
    }

  private def requestDuration = 200

  val route =
    respondWithHeaders(
      `Access-Control-Allow-Origin`(HttpOriginRange("https://b.localhost.local:8081")),
//      `Access-Control-Allow-Origin`.*,
      `Access-Control-Allow-Credentials`(true),
      `Access-Control-Allow-Headers`("Authorization", "Content-Type", "X-Requested-With")
    ) {
      setCookie(
        h.head, h.tail: _*
      ) {
        logRequestResult("Requests", Logging.InfoLevel) {
          path("random") {
            delayedComplete(requestDuration) {
              "" + Random.nextInt(100)
            }
          } ~ path("count") {
            delayedComplete(requestDuration) {
              "" + counter.getAndIncrement()
            }
          }
        }
      }
    }

  val https: HttpsConnectionContext = ConnectionContext.https(
    sslContext = ContextFactory.createSslCtx,
    clientAuth = Some(TLSClientAuth.want)
  )

//  Http().setDefaultServerHttpContext(https)
  val bindingFuture = Http().bindAndHandle(route, host, port, https)

  logger.info(s"Server online at https://$host:$port")
  logger.info("Press RETURN to stop")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
