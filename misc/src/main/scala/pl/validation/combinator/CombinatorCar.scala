package pl.validation.combinator

import pl.car._
import pl.validation._

import scalaz.{-\/, \/, \/-}

case class CombinatorCar private(engine: Option[Engine], tires: List[Tire], state: CarState) {

  private def copy(engine: Option[Engine] = this.engine, tires: List[Tire] = this.tires, state: CarState = this.state) =
    CombinatorCar(engine, tires, state)

}

object CombinatorCar {
  val bare = \/-(CombinatorCar(None, List.empty, Standing))

  def withEngine(newEngine: Engine)(carDisjunction: CarError \/ CombinatorCar): CarError \/ CombinatorCar = {
    carDisjunction.flatMap {
      car ⇒
        if (car.engine.isDefined)
          -\/(EngineAlradyPresent)
        else \/-(car.copy(engine = Some(newEngine)))
    }
  }

  def withTire(newTire: Tire)(carDisjunction: CarError \/ CombinatorCar): CarError \/ CombinatorCar = {
    carDisjunction.flatMap {
      car ⇒
        if (car.tires.size >= 4)
          -\/(TooManyTires)
        else \/-(car.copy(tires = newTire :: car.tires))
    }
  }

  def move(carDisjunction: CarError \/ CombinatorCar) = {
    carDisjunction.flatMap {
      car ⇒
        if (car.engine.isEmpty)
          -\/(NoEngine)
        else if (car.engine.contains(BrokenEngine))
          -\/(CheckEngine)
        else if (car.tires.size != 4)
          -\/(InsufficientNumberOfTires)
        else
          \/-(car.copy(state = Moving))
    }
  }
}