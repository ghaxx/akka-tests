package pl.http.clients

import java.util.concurrent.Executors

import org.slf4j.LoggerFactory
import pl.performance.Timer

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

object AkkaHttpClient extends App {

  val logger = LoggerFactory.getLogger(AkkaHttpClient.getClass)

  import akka.actor.ActorSystem
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model._
  import akka.stream.ActorMaterializer

  import scala.concurrent.Future

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
//  implicit val executionContext = system.dispatcher
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())
//  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(8))

  Await.result(Future.sequence((1 to 10).map { _ => Future { Thread.sleep(10)}}), 1 second)

  val r1: Future[HttpResponse] =
    Http().singleRequest(HttpRequest(uri = "pl.http://localhost:8080/random"))
  val r2: Future[HttpResponse] =
    Http().singleRequest(HttpRequest(uri = "pl.http://localhost:8080/random"))
  val r3: Future[HttpResponse] =
    Http().singleRequest(HttpRequest(uri = "pl.http://localhost:8080/random"))


  val t1 = Timer("time sim")
  for {
    a <- r1
    b <- r2
    c <- r3
  } yield{
    logger.info(t1.status)
    logger.info(s"Got: $a $b")
  }

  val t2 = Timer("time seq")
  for {
    a <- Http().singleRequest(HttpRequest(uri = "pl.http://localhost:8080/random"))
    b <- Http().singleRequest(HttpRequest(uri = "pl.http://localhost:8080/random"))
    c <- Http().singleRequest(HttpRequest(uri = "pl.http://localhost:8080/random"))
  } yield{
    logger.info(t2.status)
    logger.info(s"Got: $a $b")
  }

  Thread.sleep(2000)
}
