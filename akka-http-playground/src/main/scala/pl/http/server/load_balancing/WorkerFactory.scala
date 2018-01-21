package pl.http.server.load_balancing

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Flow
import com.typesafe.config.Config

class WorkerFactory {

  def newProxyActor(config: Config): Flow[HttpRequest, HttpResponse, Any] = {
//    implicit val actorSystem = ActorSystem("Proxy system", config)
//    Route.handlerFlow(new Proxy(config.getString("akka.remote.netty.tcp.hostname")).route)
    ???
  }

}
