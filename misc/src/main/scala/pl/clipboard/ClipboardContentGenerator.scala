package pl.clipboard

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

import scala.io.Source

object ClipboardContentGenerator extends App {
  var iteration = 20

  while(true) {

    println(f"$iteration%03d")
    val myString = Source.fromResource("clipboard/trade.xml")
      .mkString
      .replace("TST-KUBA-001", f"TST-KUBA-$iteration%03d")
      .replace("%initialValue%", s"${iteration * 10000000}")
    val stringSelection = new StringSelection(myString)
    val clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard()
    clpbrd.setContents(stringSelection, null)
    System.in.read()
    iteration += 1
  }
}
