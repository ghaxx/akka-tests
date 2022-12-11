import java.util.stream

import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, JNothing, JValue}

import scala.compat.java8.StreamConverters.RichStream

object JsonListParsing extends App {

  val l =
    """
      |{
      |            "A": "400",
      |            "B": "100",
      |            "C": "DEM",
      |            "D": "USD",
      |            "E": "80029898",
      |            "F": "1.64110",
      |            "G": "0 "
      |
      |        },
      |{
      |            "A": "401",
      |            "B": "101",
      |            "C": "USD",
      |            "D": "DEM",
      |            "E": "8",
      |            "F": "10 ",
      |            "G": "2",
      |
      |        },""".stripMargin

  implicit val formats = DefaultFormats

  import scala.collection.JavaConverters._
  println(l.lines().toScala.toList)

  var p: Stream[String] = l.lines().toScala
  var i = 0
  var done = false
  var tmp = ""
  while (!done) {
    p match {
      case scala.Stream.Empty => done = true
      case f #:: tail =>
        tmp += f
        println("""tmp = """ + tmp)
        try {
          Serialization.read[JValue](tmp) match {
            case JNothing =>
            case x =>
              println(Serialization.write(x))
              tmp = ""
          }
        } catch {
          case _: Throwable =>
        }
        p = tail
    }


  }

}
