package pl.scalaz.disjunctions

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scalaz._
import scalaz.Scalaz._
import scalaz.EitherT._
import scalaz.{EitherT, \/, \/-, -\/}

object TestFD extends App {

//  type A[X] = Future[String \/ X]
//
//  def a(i: Int): A[Int] = Future {
//    Thread.sleep(10)
//    if (i == 0)
//      -\/("Invalid")
//    else
//      \/-(60 / i)
//  }
//
//
//  val r = for {
//    j ← a(0) |> eitherT
////    i ← eitherT(a(10))
//    i ← a(10) |> eitherT
//  } yield i + j
//
//  val c = Await.result(r.run, 5 seconds)
//
//  println("c = " + c)
}
