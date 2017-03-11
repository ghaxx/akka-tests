package pl.misc

import java.util.concurrent.atomic.AtomicInteger

import org.scalatest.{BeforeAndAfterEach, FlatSpec}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class LazyFutureTest extends FlatSpec with BeforeAndAfterEach {

  var i: AtomicInteger = null
  var c: AtomicInteger = null

  def time = {
    s"time: ${i.incrementAndGet()}"
  }

  override protected def beforeEach(): Unit = {
    i = new AtomicInteger(0)
    c = new AtomicInteger(0)
    Thread.sleep(200)
    println("--------------------------")
  }

  they should "evaluate once at first use" in {
    println(time)

    lazy val a = Future {
      println("in future")
      c.incrementAndGet()
    }

    println("waiting...")
    Thread.sleep(100)
    println("solving")
    val r = for {
      _ <- a
      r <- a
    } yield {
      val c = for {
        _ <- a
        r <- a
      } yield {
        Thread.sleep(100)
        println(s"$time -> inner $r")
      }
      println(s"$time => outer $r")
      r
    }

    val res = Await.result(r, 5 seconds)
    println(s"$time -> result: $res")
  }

  it should "evaluate once right at definition" in {
    println(time)

    val a = Future {
      println("in future")
      c.incrementAndGet()
    }

    println("waiting...")
    Thread.sleep(100)
    println("solving")
    val r = for {
      _ <- a
      r <- a
    } yield {
      val c = for {
        _ <- a
        r <- a
      } yield {
        println(s"$time -> inner $r")
      }
      println(s"$time => outer $r")
      r
    }

    val res = Await.result(r, 5 seconds)
    println(s"$time -> result: $res")
  }

  it should "evaluate at every use" in {
    println(time)

    def a = Future {
      println("in future")
      c.incrementAndGet()
    }

    println("waiting...")
    Thread.sleep(100)
    println("solving")
    val r = for {
      _ <- a
      r <- a
    } yield {
      val c = for {
        _ <- a
        r <- a
      } yield {
        println(s"$time -> inner $r")
      }
      println(s"$time => outer $r")
      r
    }

    val res = Await.result(r, 5 seconds)
    println(s"$time -> result: $res")
  }
}
