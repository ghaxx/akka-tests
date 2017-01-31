package pl

import pl.car._
import pl.validation.combinator.CombinatorCar

//import scalaz.Semigroup

package object monoids {

  case class Car(
    engine: Option[Engine] = None,
    tires: List[Tire] = List.empty,
    state: CarState = Standing
  )

  trait ManufactureProcess

  case class TotalProgress(car: Car) extends ManufactureProcess
  case class AddEngine(engine: Engine) extends ManufactureProcess

//  implicit val carToSemigroup = new Semigroup[ManufactureProcess] {
//    def append(f1: ManufactureProcess, f2: => ManufactureProcess): ManufactureProcess = {
//      f1 match {
//        case TotalProgress(car) =>
//          f2 match {
//            case AddEngine(engine) ⇒ TotalProgress(car.copy(engine = Some(engine)))
//          }
//      }
//    }
//  }


}
