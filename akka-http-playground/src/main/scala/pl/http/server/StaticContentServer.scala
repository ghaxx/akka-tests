package pl.http.server

import java.util.concurrent.atomic.AtomicLong

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model.{ContentType, HttpCharsets, HttpEntity, HttpResponse, MediaTypes, StatusCodes, Uri}
import akka.http.scaladsl.server.ContentNegotiator.Alternative.MediaType
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.io.IOUtils

import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.Random

object StaticContentServer extends App with LazyLogging {
  implicit val system = ActorSystem("main-system", ConfigFactory.parseResources("akka-server.conf"))
  implicit val timeout = Timeout(3 seconds)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val counter = new AtomicLong()

  val route =
    logRequestResult("Requests", Logging.InfoLevel) {
      get {
        extractRequest { request =>
          complete {
            val resourcePath = request.uri.path.tail.toString()
            val file = uriPathToResourcePath(request.uri.path)
            println("""file = """ + file)
            logger.info(s"Serving resource $resourcePath")
            loadResource(resourcePath)
          }
        }
      }
    }

  def uriPathToResourcePath(path: Uri.Path): String = {
    println("""path.head = """ + path.head)
    println("""path.tail = """ + path.tail)
    if (path.tail.isEmpty) {
      if (path.head.toString == "/" || path.head.toString == "") {
        return path.tail.toString() + "index.html"
      } else {
        path.toString()
      }
    } else {
      path.head + uriPathToResourcePath(path.tail)
    }
  }

  def loadResource(resourcePath: String): HttpResponse = {
    try {
      val bytes = IOUtils.toByteArray(getClass.getClassLoader.getResourceAsStream(resourcePath))
      val contentType = ContentType(MediaTypes.forExtension(resourcePath.substring(resourcePath.lastIndexOf('.'))), () => HttpCharsets.`UTF-8`)
      val entity = HttpEntity(contentType = contentType, bytes = bytes)
      HttpResponse(StatusCodes.OK, entity = entity)
    } catch {
      case e: NullPointerException =>
        HttpResponse(StatusCodes.NotFound, entity = HttpEntity(s"$resourcePath not found"))
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  logger.info("Server online at http://localhost:8080")
  logger.info("Press RETURN to stop")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
