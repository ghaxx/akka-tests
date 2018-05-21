package pl.tracing

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, KillSwitches, Supervision}
import scala.concurrent.duration._

class KillSwitchOnRestartTest extends pl.MyFreeSpec {

  "kill stream after restart" in {
    lazy val decider: Supervision.Decider = {
      throwable =>
        log(s"Failure in stream: ${throwable.getMessage}")
        Supervision.Restart
    }
    implicit val system = ActorSystem("test")
    implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system)
      .withSupervisionStrategy(decider))
    val killswitch = KillSwitches.shared("test")

    Source(Iterator.continually(List(1, 2, 3, 4, 5)).flatten.toStream.map{i => Thread.sleep(100); i})
      .map {
        i =>
          Thread.sleep(100)
          log(s"base: $i")
          if (i % 3 == 0) throw new RuntimeException("No mod 3!")
          i * i
      }
      .via(killswitch.flow)
      //      .map {
      //        i =>
      //          log(s"square: $i")
      //          i
      //      }
      .runWith(Sink.ignore)
    Thread.sleep(2000)
    log("-> Done")
    killswitch.abort(new RuntimeException("Stop that stream"))
    killswitch.shutdown()
  }
  override implicit def patienceConfig: PatienceConfig = PatienceConfig(20 seconds, 500 millis)
}
