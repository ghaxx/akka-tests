package pl.errors

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scalaz.concurrent.Task
import scalaz._
import scalaz.Scalaz._

object Tasks extends App {


  def f(a: String): Task[Bad \/ Int] = Task {
    Some(a).map(_.toInt).fold(-\/(Bad("No number?")): Bad \/ Int) {
      i ⇒
        if (i < 0) -\/(Bad("Number should be greater than 0"))
        else \/-(i)
    }
  }

  val r3 = for {
    a ← f("1").attempt
    b ← f("b").attempt
    c ← f("-1").attempt
    d ← f("7").attempt
  } yield {
    List(a, b, c, d).map(x ⇒ x.flatMap(y ⇒ y)).traverseU(x ⇒ x.validationNel)
  }

//  r3.get.step {
//    x ⇒ println("""x = """ + x)
//  }

  println("""r3.unsafePerformSync = """ + r3.unsafePerformSync)

  Thread.sleep(2000)
}
