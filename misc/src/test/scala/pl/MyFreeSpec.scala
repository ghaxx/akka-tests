package pl

import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.{FreeSpecLike, FunSuite, Inside, Matchers}

trait MyFreeSpec
  extends FreeSpecLike
    with Matchers
    with Inside
    with Eventually
    with MyLittleHelper
    with ScalaFutures

