package pl.options

import scalaz._
import Scalaz._

object OperatingOnOptions extends App {
  val a = Some(3)
  val b = Some(5)

  println("""(a ++ b) = """ + (a ++ b))
  println("""(None ++ Some(3)) = """ + (None ++ Some(3)))
  println("""(None ++ None) = """ + (None ++ None))
}
