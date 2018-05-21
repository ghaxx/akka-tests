package pl

import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.{FlatSpec, Inside, Matchers}
import scala.concurrent.duration._
abstract class MySpec
  extends FlatSpec
    with Matchers
    with Inside
    with Eventually
    with ScalaFutures
    with MyLittleHelper {
  override implicit def patienceConfig: PatienceConfig = PatienceConfig(60 seconds, 500 millis)
}

