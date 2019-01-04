package pl.http.server

import akka.NotUsed
import akka.event.Logging
import akka.http.scaladsl.common.{CsvEntityStreamingSupport, EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{Flow, Framing, Source}
import akka.util.ByteString
import pl.http.server.data.ANumber
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.model._
import akka.http.javadsl.{common, model => jm}

class DataStreaming {

  import data.Data._

//        .withFramingRenderer(Flow[ByteString].intersperse(ByteString(">>>"), ByteString(" ||| "), ByteString("<<<")))

//  implicit val csvStreamingSupport: CsvEntityStreamingSupport =
//    new CsvEntityStreamingSupport(100)

  val route = path("stream" / "json" / LongNumber) { size =>
    implicit val jsonStreamingSupport: EntityStreamingSupport = EntityStreamingSupport.json()
    complete(jsonStream(size))
  } ~ path("stream" / "text" / LongNumber) { size =>
//    implicit val jsonStreamingSupport: EntityStreamingSupport = new TextEntityStreamingSupport(8*1024)
    implicit val jsonStreamingSupport: EntityStreamingSupport = new TextSup
    complete(numberStream(size))
  } ~ path("stream" / "csv" / LongNumber) { size =>
//    implicit val jsonStreamingSupport: EntityStreamingSupport = new TextEntityStreamingSupport(8*1024)
    implicit val jsonStreamingSupport: EntityStreamingSupport = EntityStreamingSupport.csv()
    complete(numberStream(size))
  }

 private  def jsonStream(size: Long): Source[ANumber, NotUsed] =
    Source(1L to size).map {
      i =>
//        Thread.sleep(200)
        ANumber(i)
    }

  private def numberStream(size: Long): Source[String, NotUsed] =
    Source(1L to size).map {
      i =>
//        Thread.sleep(200)
        ""+i
    }

}

class TextSup extends  EntityStreamingSupport {
  def supported: ContentTypeRange = ContentTypeRange(ContentTypes.`text/plain(UTF-8)`)
  def contentType: ContentType = ContentTypes.`text/plain(UTF-8)`
  def framingDecoder: Flow[ByteString, ByteString, NotUsed] = Framing.delimiter(ByteString("\n"), 8*1024)
  def framingRenderer: Flow[ByteString, ByteString, NotUsed] = Flow[ByteString].intersperse(ByteString("\n"))
  override def withSupported(range: jm.ContentTypeRange): EntityStreamingSupport = this
  override def withContentType(range: jm.ContentType): EntityStreamingSupport = this
  def parallelism: Int = 1
  def unordered: Boolean = false
  def withParallelMarshalling(parallelism: Int, unordered: Boolean): EntityStreamingSupport = this
}
