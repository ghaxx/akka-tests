package pl.validation.monadic

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

import pl.MySpec
import pl.car._
import pl.validation._

import scala.collection.mutable
import scala.concurrent._
import scala.concurrent.duration._
import scalaz.Scalaz._
import scalaz._

class MonadicCarTest extends MySpec {

  override implicit val patienceConfig = PatienceConfig(15 seconds, 5 millis)
  implicit lazy val global: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(20))
  val iterations = 100000

  "Monadic car" should "be completed" in {
    val carDisjunction = ProperCar.bare
      .withEngine(V12)
      .withTire(Toyo)
      .withTire(Toyo)
      .withTire(Goodyear)

    inside(carDisjunction) {
      case car: ProperCar ⇒
        car.engine shouldBe Some(V12)
        car.tires should contain theSameElementsAs List(Toyo, Toyo, Goodyear)
    }
  }

  it should "not move" in {
    val carDisjunction = ProperCar.bare.withEngine(V12).withTire(Toyo)

    val moving = carDisjunction.move

    moving shouldBe CarMonadError(InsufficientNumberOfTires)
  }

  it should "not compile" ignore {
    //    ProperCar(None, List.empty, Standing)
    //    ProperCar.bare.copy()
    //    """ProperCar(None, List.empty, Standing)""" shouldNot compile
    //    """ProperCar.bare.copy()""" shouldNot compile
  }

  it should "work in for comprehensions for some reason" in {
    val car = for {
      bare ← ProperCar.bare
      withEngine ← bare.withEngine(V12)
      oneTire ← withEngine.withTire(Toyo)
    } yield oneTire
    car.move shouldBe CarMonadError(InsufficientNumberOfTires)
  }

  it should "warm up threads" in {
    val atomic = new AtomicInteger(iterations)
    (1 to iterations) foreach {
      _ ⇒ Future {
        atomic.decrementAndGet()
      }
    }
    eventually {
      atomic.get shouldBe 0
    }
  }
  (1 to 3) foreach {
    i ⇒
      it should s"work nicely with EitherT $i" in {
        type FutureCar = EitherT[Future, CarError, ProperCar]
        def futureCar: FutureCar = EitherT {
          Future {
            ProperCar.bare
              .withEngine(V12)
              .withTire(Toyo)
              .withTire(Toyo)
              .withTire(Goodyear)
              .withTire(Goodyear)
              .disjunction
          }
        }

        val start = System.currentTimeMillis()
        val cars = mutable.ArrayBuffer.empty[ProperCar]
        val atomic = new AtomicInteger(iterations)
        (1 to iterations) foreach {
          _ ⇒
            val r = for {
              car ← futureCar
              anotherCar ← futureCar
            } yield {
              atomic.decrementAndGet()
              cars += car
              cars += anotherCar
            }
        }
        eventually {
          atomic.get shouldBe 0
          cars.size should be > iterations
          val duration = System.currentTimeMillis() - start
          println(s"This took $duration ms")
        }
      }

      it should s"work nicely with futures $i" in {
        def futureCar: Future[CarError \/ ProperCar] = Future {
          ProperCar.bare
            .withEngine(V12)
            .withTire(Toyo)
            .withTire(Toyo)
            .withTire(Goodyear)
            .withTire(Goodyear)
            .disjunction
        }

        val start = System.currentTimeMillis()
        val cars = mutable.ArrayBuffer.empty[ProperCar]
        val atomic = new AtomicInteger(iterations)
        (1 to iterations) foreach {
          _ ⇒
            val r = for {
              car ← futureCar ▹ EitherT.eitherT
              anotherCar ← futureCar ▹ EitherT.eitherT
            } yield {
              atomic.decrementAndGet()
              cars += car
              cars += anotherCar
            }
        }
        eventually {
          atomic.get shouldBe 0
          cars.size should be > iterations
          val duration = System.currentTimeMillis() - start
          println(s"This took $duration ms")
        }

      }
  }
}
