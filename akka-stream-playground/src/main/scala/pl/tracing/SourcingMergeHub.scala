package pl.tracing

object SourcingMergeHub extends App {

  import scala.concurrent.duration._

  implicit val system = ActorSystem("main-system")
  implicit val timeout = Timeout(3 seconds)
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system).withInputBuffer(initialSize = 8, maxSize = 64))


  val p = Promise[Done]
  val a = KillSwitches.shared("A")

  val m = MergeHub
    .source[Int]
      .via(a.flow)
    .map {
      x =>
        Thread.sleep(1000)
        println(x)
        x
    }
    //    .runWith(Sink.fold(List.empty[Int])((l, e) => e :: l))
    .watchTermination()((a, b) => {
    p.completeWith(b)
    a
  })
    .to(Sink.fold(List.empty[Int])((l, e) => e :: l))
    .run()

  //  Source(1 to 5000).runWith(m)
  //  Source
  //    .fromFuture(Future.never)
  //    .keepAlive(2 seconds, () => {
  //      println("Keep alive!")
  //      -1})
  //    .runWith(m)
  (1 to 5).foreach {
    i =>
      println(s"Put $i")
      val used = Source.single(i).to(m).run()
      try {
        Await.result(p.future, 1 seconds)
        println("p done")
      } catch {
        case t: Throwable => println("p not done")
      }
    //      val used2 = Source.single(i).runWith(m)
    //      Await.result(used, 2 seconds)
    //      Thread.sleep(3000)
  }
  a.shutdown()
system.terminate()
  try {
    Await.result(p.future, 3 seconds)
    println("p done")
  } catch {
    case t: Throwable => println("p not done")
  }
}
