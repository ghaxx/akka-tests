package pl.validation.disjunction

import org.scalatest.{FlatSpec, Matchers}
import pl.MySpec
import pl.car.{Goodyear, InsufficientNumberOfTires, Toyo, V12}

import scalaz.{-\/, \/-}

class DisjunctionCarTest extends MySpec {

  "Disjunction car" should "be completed" in {
    val carDisjunction = DisjunctionCar.bare
      .flatMap(_.withEngine(V12))
      .flatMap(_.withTire(Toyo))
      .flatMap(_.withTire(Toyo))
      .flatMap(_.withTire(Goodyear))

    inside(carDisjunction) {
      case \/-(car) ⇒
        car.engine shouldBe Some(V12)
        car.tires should contain theSameElementsAs List(Toyo, Toyo, Goodyear)
    }
  }

  it should "not move" in {
    val carDisjunction = DisjunctionCar.bare.flatMap(_.withEngine(V12)).flatMap(_.withTire(Toyo))

    val moving = carDisjunction.flatMap(_.move)

    moving shouldBe -\/(InsufficientNumberOfTires)
  }

}
