package pl.http.server

import akka.NotUsed
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Source
import pl.http.server.data.ANumber

class DataWebSocket {

  import data.Data._

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport.json()
//        .withFramingRenderer(Flow[ByteString].intersperse(ByteString(">>>"), ByteString(" ||| "), ByteString("<<<")))

//  implicit val csvStreamingSupport: CsvEntityStreamingSupport =
//    new CsvEntityStreamingSupport(100)

  val route = path("stream" / "json" / LongNumber) { size =>
    complete(jsonStream(size))
  } ~ path("stream" / "numbers" / LongNumber) { size =>
    complete(numberStream(size))
//    complete("OK")
  }

 private  def jsonStream(size: Long): Source[ANumber, NotUsed] =
    Source(1L to size).map {
      i =>
        Thread.sleep(200)
        ANumber(i)
    }

  private def numberStream(size: Long): Source[String, NotUsed] =
    Source(1L to size).map {
      i =>
        Thread.sleep(200)
        ""+i
    }
}
