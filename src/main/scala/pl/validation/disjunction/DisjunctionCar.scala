package pl.validation.disjunction

import pl.car._
import pl.validation._

import scalaz.{-\/, \/, \/-}

case class DisjunctionCar private(engine: Option[Engine], tires: List[Tire], state: CarState) {

  def withEngine(newEngine: Engine): CarError \/ DisjunctionCar = {
    if (engine.isDefined)
      -\/(EngineAlradyPresent)
    else \/-(copy(engine = Some(newEngine)))
  }

  def withTire(newTire: Tire): CarError \/ DisjunctionCar = {
    if (tires.size >= 4)
      -\/(TooManyTires)
    else \/-(copy(tires = newTire :: tires))
  }

  def move = {
    if (engine.isEmpty)
      -\/(NoEngine)
    else if (engine.contains(BrokenEngine))
      -\/(CheckEngine)
    else if (tires.size != 4)
      -\/(InsufficientNumberOfTires)
    else
      \/-(copy(state = Moving))
  }

  private def copy(engine: Option[Engine] = this.engine, tires: List[Tire] = this.tires, state: CarState = this.state) =
    DisjunctionCar(engine, tires, state)
}

object DisjunctionCar {
  val bare = \/-(DisjunctionCar(None, List.empty, Standing))

  def withEngine(newEngine: Engine)(carDisjunction: CarError \/ DisjunctionCar) =
    carDisjunction.flatMap(_.withEngine(newEngine))

  def withTire(newTire: Tire)(carDisjunction: CarError \/ DisjunctionCar) =
    carDisjunction.flatMap(_.withTire(newTire))

  def move(carDisjunction: CarError \/ DisjunctionCar) =
    carDisjunction.flatMap(_.move)

}