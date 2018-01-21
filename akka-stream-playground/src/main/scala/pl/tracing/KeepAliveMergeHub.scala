package pl.tracing

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, GraphDSL, MergeHub, Sink, Source, UnzipWith, ZipWith}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, FlowShape}
import akka.util.Timeout

import scala.concurrent.Future

object KeepAliveMergeHub extends App {

  import scala.concurrent.duration._

  implicit val system = ActorSystem("main-system")
  implicit val timeout = Timeout(3 seconds)
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system).withInputBuffer(initialSize = 8, maxSize = 64))

  val graph = Flow.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    //    val bcast = b.add(Broadcast[Int](3))
    val bcast = b.add(UnzipWith[Int, Int, Int, Int](x => (x, -x, -10000)))

    //    bcast.buffer(10, OverflowStrategy.dropHead) ~> Sink.ignore
    //    bcast.buffer(10, OverflowStrategy.dropHead) ~> Sink.ignore
    //    bcast.buffer(10, OverflowStrategy.dropHead) ~> Sink.ignore
//    val merge = b.add(Merge[Int](3))
    val zip = b.add(ZipWith((a: Int, b: Int, c: Int) => (a, b, c)))
    //    bcast.out0.map{
    //      x => println(s"1>$x")
    //    } ~> merge.in(0)
    bcast.out0 ~> Flow[Int].map {
      x => println(s"0>$x"); x
    } ~> zip.in0
    bcast.out1 ~> Flow[Int].map {
      x => println(s"1>$x");
        Thread.sleep(10)
        x
    } ~> zip.in1
    bcast.out2 ~> Flow[Int].keepAlive(4 seconds, () => {
      println("Keep alive!")
      -1}).map({
      x => println(s"2>$x"); x
    }) ~> zip.in2

    FlowShape(bcast.in, zip.out)
//    FlowShape(bcast.in, o.outlet)
  })

  val m = MergeHub
    .source[Int]
    .via(graph)
    .mapAsync(1) {
      x =>
//        println(x)
        Thread.sleep(1000)
        Future successful x
    }
    .map(println)
    .to(Sink.ignore)
    .run()

  Source(1 to 5000).runWith(m)
//  Source
//    .fromFuture(Future.never)
//    .keepAlive(2 seconds, () => {
//      println("Keep alive!")
//      -1})
//    .runWith(m)
  (1 to 50000).foreach {
    i =>
      val used = Source.single(i).to(m).run()
//      Await.result(used, 2 seconds)
      Thread.sleep(3000)
  }

}
