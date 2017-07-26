package pl

import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.{FunSuite, Inside, Matchers}

abstract class MyFunSpec
  extends FunSuite
    with Matchers
    with Inside
    with Eventually
    with MyLittleHelper
    with ScalaFutures

