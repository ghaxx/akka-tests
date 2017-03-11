package pl.car

trait CarError {
  def message: String
}

case object NoEngine extends CarError {
  val message = "No engine in car"
}

case object InsufficientNumberOfTires extends CarError {
  val message = "Car needs 4 tires"
}


case object TooManyTires extends CarError {
  val message = "Cannot put more than 4 tires"
}

case object CheckEngine extends CarError {
  val message = "Engine is not working properly"
}

case object EngineAlradyPresent extends CarError {
  val message = "Cannot insert two engines"
}
