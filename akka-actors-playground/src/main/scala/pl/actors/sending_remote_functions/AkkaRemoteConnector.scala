package pl.actors.sending_remote_functions

import akka.actor.{ActorSystem, Identify}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await

object AkkaRemoteConnector extends App {
  import akka.pattern.ask

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  implicit val timeout: Timeout = 10 seconds

//  val url = "otccc1.systems.uk.hsbc:9200"
  val url = "gbl03817.systems.uk.hsbc:9200"
  val a = s"akka.tcp://JLS-Akka@$url/user/imLimitProvider"
  val system = ActorSystem("Test-Akka", ConfigFactory.parseResources("akka-test.conf"))
  val actorFut = system.actorSelection(a).resolveOne

  val rFut = for {
    actor <- actorFut
    res <- actor ? Identify(1)
  } yield res

  val r = Await.result(rFut, 10 seconds)
  println(s"""r = ${r}""")
}
