package pl.validation.monadic

import pl.car._
import pl.validation._

import scalaz.{-\/, \/, \/-}

/**
  * If only this was possible: MonadicCar extends CarError \/ ProperCar
  */
sealed trait MonadicCar {
  def withEngine(newEngine: Engine): MonadicCar
  def withTire(newTire: Tire): MonadicCar
  def move: MonadicCar
  def disjunction: CarError \/ ProperCar = this match {
    case CarMonadError(error) ⇒ -\/(error)
    case p: ProperCar ⇒ \/-(p)
  }

  def flatMap(f: ProperCar ⇒ MonadicCar): MonadicCar =
    this match {
      case e: CarMonadError ⇒ e
      case p: ProperCar ⇒ f(p)
    }

  def map[B](f: ProperCar ⇒ ProperCar): MonadicCar =
    this match {
      case e: CarMonadError ⇒ e
      case p: ProperCar ⇒ f(p)
    }

}

object MonadicCar {
  def identity(x: MonadicCar) = x
}

case class CarMonadError(error: CarError) extends MonadicCar {
  def withEngine(newEngine: Engine): MonadicCar = flatMap(identity)
  def move: MonadicCar = this
  def withTire(newTire: Tire): MonadicCar = this
}

case class ProperCar private(engine: Option[Engine], tires: List[Tire], state: CarState) extends MonadicCar {
  def withEngine(newEngine: Engine): MonadicCar = flatMap {
    car ⇒
      if (car.engine.isDefined)
        CarMonadError(EngineAlradyPresent)
      else car.copy(engine = Some(newEngine))
  }

  def withTire(newTire: Tire): MonadicCar = {
    if (tires.size >= 4)
      CarMonadError(TooManyTires)
    else copy(tires = newTire :: tires)
  }

  def move: MonadicCar = {
    if (engine.isEmpty)
      CarMonadError(NoEngine)
    else if (engine.contains(BrokenEngine))
      CarMonadError(CheckEngine)
    else if (tires.size != 4)
      CarMonadError(InsufficientNumberOfTires)
    else
      copy(state = Moving)
  }

  private def copy(engine: Option[Engine] = this.engine, tires: List[Tire] = this.tires, state: CarState = this.state) =
    ProperCar(engine, tires, state)
}

object ProperCar {
  val bare: MonadicCar = ProperCar(None, List.empty, Standing)
}