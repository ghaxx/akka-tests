package pl.lifting

import org.scalatest.concurrent.ScalaFutures
import pl.MySpec

import scala.concurrent.Future
import scalaz._
import Scalaz._
import scala.concurrent.ExecutionContext.Implicits.global
class LiftingTest extends MySpec with ScalaFutures {

  val n = 1000

  it should "work fast" in {

    logTime("bypassing") {
        val c = f(n).flatMap {
          case \/-(a) ⇒
            val b = (a.traverseU(i ⇒ g(i)))
            val c = b.map {
              x ⇒
                val z = x.map {
                  y ⇒
                    val d = y.flatMap(h)
                    d
                }
                z
            }
            c
          case err@ -\/(_) ⇒ Future successful List(err)
        }
//        c.futureValue
//      println(c.map(_.toOption.get).sum)
    }

//    time("lifting") {
//        val c = for {
//          i ← f(n) ▹ EitherT.apply
//          a ← i
//          b ← g(a) ▹ EitherT.apply
//          c ← h(b) ▹ Future.successful ▹ EitherT.apply
//        } yield c
////        c.run.futureValue
////      println(c.map(_.toOption.get).sum)
//    }

   val a = EitherT(f(n)).map(x ⇒ (x.traverseU(g)))
  }

  def f(n: Int): Future[String \/ List[Int]] = Future {
    \/-((1 to n).toList)
  }

  def g(i: Int): Future[String \/ Int] = Future {
    \/-(i + 1)
  }

  def h(i: Int): String \/ Int = \/-(i * 2)

}
