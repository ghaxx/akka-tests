package pl

import java.text.NumberFormat
import java.util.Locale

import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * Does creating identical case classes impact memory?
  */
object CaseClassesMemory extends App {
  //w34sefge45hrg
  val logger = LoggerFactory.getLogger(getClass)

  val l = ListBuffer.empty[T]
  Stream.from(1).map {
    i =>
      if (i % 1000 == 0) mem(i)
      l.append(T("123", 123))
      i
  }.take(1000000).toList

  case class T(
    s: String,
    i: Int,
    i0: Double = Double.MaxValue,
    i1: Double = Double.MaxValue,
    i2: Double = Double.MaxValue,
    i3: Double = Double.MaxValue,
    i4: Double = Double.MaxValue,
    i5: Double = Double.MaxValue,
    i6: Double = Double.MaxValue,
    i7: Double = Double.MaxValue,
    i8: Double = Double.MaxValue,
    i9: Double = Double.MaxValue,
    i10: Double = Double.MaxValue,
    i11: Double = Double.MaxValue,
    i12: Double = Double.MaxValue,
    i13: Double = Double.MaxValue,
    i14: Double = Double.MaxValue,
    i15: Double = Double.MaxValue,
    i16: Double = Double.MaxValue,
    i17: Double = Double.MaxValue,
    i18: Double = Double.MaxValue,
    i19: Double = Double.MaxValue,
    i20: Double = Double.MaxValue,
    i21: Double = Double.MaxValue,
    i22: Double = Double.MaxValue,
    i23: Double = Double.MaxValue,
    i24: Double = Double.MaxValue,
    i25: Double = Double.MaxValue,
    i26: Double = Double.MaxValue,
    i27: Double = Double.MaxValue,
    i28: Double = Double.MaxValue,
    i29: Double = Double.MaxValue,
    i30: Double = Double.MaxValue,
    i31: Double = Double.MaxValue,
    i32: Double = Double.MaxValue,
    i33: Double = Double.MaxValue,
    i34: Double = Double.MaxValue,
    i35: Double = Double.MaxValue,
  )

  def mem(i: Int) = {
    System.gc()
    val rt = Runtime.getRuntime
    val usedMB = rt.totalMemory() - rt.freeMemory()
    logger.info("Memory usage @ " + i + ": " + NumberFormat.getNumberInstance(Locale.GERMANY).format(usedMB))
  }
}
