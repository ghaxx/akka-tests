package pl

import scala.util.Random

object StudentsTest extends App {

  val start = 0
  val end = start + 8
  def idx(i: Int) = (i + end) % end

  val results = (1 to 10000000) map { x =>
    val students = collection.mutable.ArrayBuffer.fill(8)(0)
    (start until end) foreach { i =>
      val direction = ((Random.nextInt(2)-0.5)*2).toInt
      students(idx(i + direction)) = students(idx(i + direction)) + 1
    }
    val count = students.count(_ == 0)
//    println(s"count = $count ($students)")
    count
  }
  println("""(results.sum / results.size) = """ + (results.sum.toDouble / results.size))

}
