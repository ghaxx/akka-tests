package pl.shapes

import java.io.StringReader
import javax.xml.parsers.SAXParserFactory

import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.{Attributes, InputSource}
import pl.MyFunSpec
import shapeless._

import scala.collection.mutable

class VisitSaxHandler extends MyFunSpec {
  import shapeless.ops._
  import shapeless.ops.function._
  import shapeless.syntax.std.function._

  case class C(v: String)

  val xml =
    """
      |<xml>
      | <a>abaca</a>
      | <b>babaca</b>
      | <c>cabaca</c>
      |</xml>
    """.stripMargin

  test("should look functional") {
    val factory = SAXParserFactory.newInstance()
    factory.setNamespaceAware(true)
    val parser = factory.newSAXParser()
    val extractors = new Extractors[AnyRef, HNil, AnyRef](List.empty)
      .append(Extractor("/xml/a") { text => text + " in A" })
      .append(Extractor("/xml/c") { text => C(text + " in C") })
      .returning[(C, String)]
    val handler = new LocalParser(
      extractors
    )
    parser.parse(new InputSource(new StringReader(xml)), handler)
    println("""handler.nodes = """ + handler.nodes)
    val r = extractors.processResults {
      (x: C, y: String) ⇒
        (x, y)
    }
    println("""r = """ + r)
  }

  trait Extractor[+T <: AnyRef] {
    def node: String
    def extract(ch: Array[Char], start: Int, length: Int): Unit
    def value: T
  }
  object Extractor {
    def apply[T <: AnyRef](_node: String)(f: String => T) = new Extractor[T] {
      val node = _node
      var valueOpt: Option[T] = None
      def value: T = valueOpt.get
      def extract(ch: Array[Char], start: Int, length: Int) =
        valueOpt = Some(f(new String(ch, start, length)))
    }
  }

  class Extractors[U <: AnyRef, H <: HList, Out](val list: List[Extractor[U]]) {
    def append[T <: U](e: Extractor[T]) = new Extractors[U, T :: H, Out](e :: list)
    def returning[T] = new Extractors[U, H, T](list)

    def processResults[F](f: F)(
      implicit fp: FnToProduct.Aux[F, H => Out]
    ) = {
      val a: H = list.foldRight[HList](HNil)((v, m) ⇒ v.value :: m).asInstanceOf[H]
      f.toProduct(a)
    }
  }


  class LocalParser[U <: AnyRef, H <: HList](find: Extractors[U, H, _]) extends DefaultHandler {
    val nodes = mutable.Map.empty[String, String]
    val path = mutable.ListBuffer.empty[String]
    var currentNode = ""

    override def startElement(uri: String, localName: String, qName: String, attributes: Attributes) = {
      path.append(qName)
      currentNode = currentNode + "/" + qName
    }
    override def characters(ch: Array[Char], start: Int, length: Int) = {
      if (find.list.exists(_.node == currentNode)) {
        nodes(currentNode) = new String(ch, start, length)
        find.list.find(_.node == currentNode).head.extract(ch, start, length)
      }
    }
    override def endElement(uri: String, localName: String, qName: String) = {
      path.remove(path.size - 1)
      currentNode = path.mkString("/", "/", "")
    }
  }
}
