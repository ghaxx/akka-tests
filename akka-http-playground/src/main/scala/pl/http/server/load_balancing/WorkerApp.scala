package pl.http.server.load_balancing

import java.util.concurrent.atomic.AtomicLong

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.http.server.{CorsSupport, DataStreaming}
import pl.http.server.load_balancing.Gateway.{logger, system}

import scala.io.StdIn

object WorkerApp extends App with CorsSupport with LazyLogging {
  new Worker("p1.conf")
//  new Worker("p2.conf")
}
