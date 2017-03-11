package pl

import org.scalatest.concurrent.Eventually
import org.scalatest.{Inside, FlatSpec, Matchers}

abstract class MySpec extends FlatSpec with Matchers with Inside with Eventually with MyLittleHelper {

}

