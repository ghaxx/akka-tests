package pl.monoids

import pl.car.{Standing, V12}
import pl.{MyFunSpec, monoids}

import scalaz.Scalaz._
import scalaz._

class MonoidsTest extends MyFunSpec {

  import monoids._

  test("Disjunction should work with monoids") {

    def c(manufactureProcess: ManufactureProcess): NonEmptyList[String] \/ ManufactureProcess = \/-(manufactureProcess)
//
//    val r = c(TotalProgress(Car())) +++ c(AddEngine(V12))
//
//    r should matchPattern {
//      case \/-(TotalProgress(Car(Some(V12), Nil, Standing))) =>
//    }
  }

}
