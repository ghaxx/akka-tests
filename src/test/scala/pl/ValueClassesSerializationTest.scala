package pl

import org.json4s.DefaultFormats
import org.json4s.native.Serialization
import org.scalatest.{FlatSpec, Matchers}

class ValueClassesSerializationTest extends FlatSpec with Matchers {

  implicit val formats = DefaultFormats
  val se7en = "se7en"

  "Wrapper value class" should "be unwrapped while serializing" in {
    val s = Serialization.write(A(Wrapper(se7en)))
    s shouldBe """{"w":"se7en"}"""
  }

  "Value" should "be wrapped again while deserializing" in {
    val r = Serialization.read[A](
      """
        {
          "w": "se7en"
        }"""
    )
    r shouldBe A(Wrapper(se7en))
  }
}

case class A(w: Wrapper)
case class Wrapper(value: String) extends AnyVal