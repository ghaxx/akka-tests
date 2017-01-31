package pl.validation.combinator

import pl.MySpec
import pl.car._
import pl.validation._

import scalaz._
import Scalaz._

class CombinatorCarTest extends MySpec {

  import CombinatorCar._

  "Combinator car" should "be completed" in {
    val carDisjunction = CombinatorCar.bare ▹ withEngine(V12) ▹ withTire(Toyo) ▹ withTire(Toyo) ▹ withTire(Goodyear)

    inside(carDisjunction) {
      case \/-(car) ⇒
        car.engine shouldBe Some(V12)
        car.tires should contain theSameElementsAs List(Toyo, Toyo, Goodyear)
    }
  }

  it should "not move" in {
    val carDisjunction = CombinatorCar.bare ▹ withEngine(V12) ▹ withTire(Toyo)
    val moving = carDisjunction ▹ move
    moving shouldBe -\/(InsufficientNumberOfTires)
  }

  it should "not be posssible but, unfortunately, is" in {
    def pillage(carDisjunction: CarError \/ CombinatorCar) = {
      carDisjunction.flatMap {
        car ⇒ \/-(CombinatorCar.bare)
      }
    }

    CombinatorCar.bare ▹ withEngine(V12) ▹ withTire(Toyo) ▹ pillage
  }
}
