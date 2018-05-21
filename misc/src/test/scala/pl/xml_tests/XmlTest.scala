package pl.xml_tests

import org.xmlunit.builder.DiffBuilder
import org.xmlunit.diff.{DOMDifferenceEngine, DifferenceEngine}
import pl.MyFreeSpec

class XmlTest extends MyFreeSpec {
  "compare xml with different tags and no short tags" in {
    val a = <a>
      <b bt1="a" bt2="b">
        <c>a</c>
      </b>
      <d dt2="b" dt1="a"/>
    </a>
    val b = <a>
      <b bt1="a" bt2="b">
        <c>a</c>
      </b>
      <d   dt1="a"  dt2="b" ></d>
    </a>
//    new DOMDifferenceEngine().compare()
    val diffs = DiffBuilder.compare(a.toString().getBytes()).withTest(b.toString().getBytes()).build().getDifferences
    println("""diffs = """ + diffs)
    diffs.iterator().hasNext shouldBe false
  }
}
