package pl.actors.typed

import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}

object Serializ extends App {
  import ID._
  implicit val formats = Serialization.formats(NoTypeHints) // + new IdSerializer

  val data = Data("id field", "Kuba", 29)

  val s = write(data)
  println(s"s: $s")
  val r = read[Data](s)
  println(s"r: $r")
}

case class ID(value: String) extends AnyVal

object ID {
  implicit def stringToId(s: String): ID = ID(s)
}

case class Data(
               id: ID,
               name: String,
               age: Int
                 )

class IdSerializer extends CustomSerializer[ID](format => (
  { case JString(e) => new ID(e)},
  { case x: ID => JString(x.value)}
  ))