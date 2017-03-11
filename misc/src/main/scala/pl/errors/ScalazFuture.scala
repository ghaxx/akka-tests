package pl.errors

import scala.concurrent.Await
import scala.concurrent.duration._
import scalaz.Scalaz._
import scalaz._
import scalaz.concurrent.Future

object ScalazFuture extends App {



  def f(a: String): Future[Bad \/ Int] = Future {
    Some(a).map(_.toInt).fold(-\/(Bad("No number?")): Bad \/ Int) {
      i ⇒
        if (i < 0) -\/(Bad("Number should be greater than 0"))
        else \/-(i)
    }
  }

  val r3 = for {
    a ← f("1")
    b ← f("b")
    c ← f("-1")
    d ← f("7")
  } yield {
    List(a, b, c, d).traverseU(x ⇒ x.validationNel)
  }

//  r3.map {
//    x ⇒ println("""x = """ + x)
//  }

//  println("""Await.result(r3.step, 5 seconds) = """ + Await.result(r3.get, 5 seconds))
    println("""r3.unsafePerformSync = """ + r3.unsafePerformSync)

  Thread.sleep(2000)
}
