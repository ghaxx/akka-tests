package pl

import org.scalatest.concurrent.Eventually
import org.scalatest.{FlatSpec, FunSuite, Inside, Matchers}

abstract class MyFunSpec extends FunSuite with Matchers with Inside with Eventually with MyLittleHelper {

}

