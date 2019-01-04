package pl.http.clients

import akka.actor.ActorSystem
import akka.stream.Supervision.Resume
import akka.stream.scaladsl.{BroadcastHub, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, OverflowStrategy}
import akka.util.Timeout
import org.asynchttpclient._
import pl.http.client.Request

import scala.concurrent.Await

object AsyncHttpClientStreaming extends App {


  import pl.http.client.ScalaAsyncHttpClient._

  //  implicit val ec = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
  //    implicit val ec = ExecutionContext.fromExecutorService(Executors.newWorkStealingPool())
  //    implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))
  import scala.concurrent.duration._

  implicit val system = ActorSystem("main-system")
  implicit val timeout = Timeout(30 seconds)
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system).withInputBuffer(initialSize = 1, maxSize = 1).withSupervisionStrategy { t =>
    println(t.getMessage)
    Resume
  })


  val cf = new DefaultAsyncHttpClientConfig.Builder()
    .setIoThreadsCount(2)
    //    .setMaxConnections(1)
    .build()
  val asyncHttpClient = new DefaultAsyncHttpClient(cf)
  //  val asyncHttpClient = new DefaultAsyncHttpClient()

    val get2 = Request.Get("http://localhost:8080/stream/text/10000")
//  val get2 = Request.Get("http://localhost:8080/big")

  val name = "async"
  val (q, q2) = Source.queue[Array[Byte]](1000, OverflowStrategy.dropHead)
    .toMat(BroadcastHub.sink(bufferSize = 8))(Keep.both).run()



  val r = q2
    .map {
      b =>
        if (b.isEmpty) print(".")
        else println("q2> " + b.map(_.toChar).mkString)
//        else println("q2> " + b.length)
        b
    }
    .runWith(Sink.fold(0) {
      case (s, i) => s + 1//i.length
    })

  var s = asyncHttpClient.asyncExecuteAsByteStream(get2, q)
//  val r2 = q2.runWith(Sink.last)
//  println(">> " + new String(Await.result(r2, 30 seconds)))
  println(">> " + Await.result(r, 30 seconds))
  println("Fin")
  //Thread.sleep(5000)
  //  s.runForeach {
  //    b =>
  //      println("> " + b.map(_.toChar).mkString)
  //  }


}

def asyncExecuteAsByteStream(request: ARequest, q:  SourceQueueWithComplete[Array[Byte]])(implicit system: ActorSystem, materializer: ActorMaterializer): Source[Array[Byte], NotUsed] = {
  val s2 = new S
  val s = Source.fromGraph(s2)
  client.executeRequest(request, new AsyncCompletionHandler[Unit] {

  override def onBodyPartReceived(content: HttpResponseBodyPart): AsyncHandler.State = {
  //          println(new String(content.getBodyPartBytes))
  //          q.synchronized{
  val x= content.getBodyPartBytes
  //          println(x.length)
  q.offer(x)
  //          }
  AsyncHandler.State.CONTINUE
}

  def onCompleted(response: AResponse): Unit = {
  //          s2.complete()
  //          q.synchronized {
  println("Done")
  q.complete()
  //          }
}

})
  s
}