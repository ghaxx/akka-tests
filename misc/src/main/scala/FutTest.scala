import scala.util.Failure

object FutTest extends App {

  import scala.concurrent._
  import scala.util.{Try, Success}
  import scala.concurrent.ExecutionContext.Implicits.global

  import scala.concurrent.duration._

  //Define type aliases to cut down on the bracket-madness
  //for this blog post format
  type ListOfFutures = List[Future[Int]]
  type FutureListOfTrys = Future[List[Try[Int]]]

  def results(l: ListOfFutures): FutureListOfTrys = {
    // An inner method which allows us to recursively decompose
    // the problem and build up the solution
    // `f` is our current result, and `remaining` is what we have left to do
    def acps(f: FutureListOfTrys, remaining: ListOfFutures): FutureListOfTrys =
    remaining match {
      case Nil => f // When we hit the end of the list, we're done
      case r :: tail =>
        f.flatMap(list =>
          // as `f` completes, use the current result `list`
          // and when `r` completes, add its result to `list`
          // and carry forward `tail` as the `remaining` for the next `acps`
          acps(r.transform(result => Success(result :: list)), tail)
        )
    }

    val a = <xml> </xml>

    // Our initial result is `Nil`, and initially `l` is what is `remaining`
    // and we need to `reverse` the result once it is done since
    // we are building up the answer in reverse order (List)
    acps(Future.successful(Nil), l).map(_.reverse)
  }
  val example = {
    List(Future(5),
      Future.failed(new Exception("foo")),
      Future("pigdog".hashCode))
  }
  val r = results(example)

  println(Await.result(r, 10 seconds))
}
