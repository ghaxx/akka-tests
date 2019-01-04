package pl.http.server

import java.io.{BufferedInputStream, FileInputStream}
import java.nio.file.{Files, Paths}
import java.util.concurrent.atomic.AtomicLong

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.{ByteString, Timeout}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.io.IOUtils

import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.{Random, Try}

object ResourcesServer extends App with CorsSupport with LazyLogging {
  implicit val system = ActorSystem("main-system", ConfigFactory.parseResources("akka-server.conf"))
  implicit val timeout = Timeout(3 seconds)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val port = Try(args(1).toInt).getOrElse(8081)
  val host = Try(args(0)).getOrElse("localhost1")

  private def getDefaultPage = {
    "/index.html"
  }

  private def getExtensions(fileName: String): String = {
    val index = fileName.lastIndexOf('.')
    if (index != 0) {
      fileName.drop(index + 1)
    } else
      ""
  }

  val route =
    logRequest("Requests", Logging.InfoLevel) {
      get {
        entity(as[HttpRequest]) { requestData =>
          complete {
            val requestedPath = requestData.uri.path.toString match {
              case "/" => getDefaultPage
              case "" => getDefaultPage
              case _ => requestData.uri.path.toString
            }
            val resourcePath = "static" + requestedPath
            try {
              logger.debug(s"Searching for resource under $resourcePath")
              val ext = getExtensions(resourcePath)
              val c: ContentType = ContentType(MediaTypes.forExtension(ext), () => HttpCharsets.`UTF-8`)
              val is = getClass.getClassLoader.getResourceAsStream(resourcePath)
//              val cnt = is.available
//              val bytes = Array.ofDim[Byte](cnt)
              val bytes = IOUtils.toByteArray(is)
//              is.read(bytes)
              is.close()
              HttpResponse(StatusCodes.OK, entity = HttpEntity(c, bytes))
            } catch {
              case _: NullPointerException =>
                logger.warn(s"Cannot serve $resourcePath")
                HttpResponse(StatusCodes.NotFound)
            }
          }
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(corsHandler(route), host, port)

  logger.info(s"Server online at http://$host:$port")
  logger.info("Press RETURN to stop")
  StdIn.readLine()
  bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
}
