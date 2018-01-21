package pl.http.server.load_balancing

import akka.actor.Actor
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, UnreachableMember}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

class WorkerActor(configPath: String) extends Actor {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive: PartialFunction[Any, Unit] = {
    case r: HttpRequest =>
      Thread.sleep(500)
      sender ! HttpResponse(entity = s"From $configPath: ${r.uri.path}")
  }
}
