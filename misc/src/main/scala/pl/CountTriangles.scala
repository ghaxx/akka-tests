package pl


/*
                                   A
                                / /\  \
                               B--C-D--E
                             /   /  \   \
                            F----G---H---I
                           /    /     \   \
                         J------K-----L----M


 */
object CountTriangles extends App {

  val points = "abcdefghijklm"
//  w34sefge45hrg
  val lines = List(
    "abfj", "acgk", "adhl", "aeim",
    "bcde", "fghi", "jklm"
  )

  def isTriangle(p1: Char, p2: Char, p3: Char): Boolean = {
    val l1 = lines.exists(l => l.contains(p1) && l.contains(p2))
    val l2 = lines.exists(l => l.contains(p2) && l.contains(p3))
    val l3 = lines.exists(l => l.contains(p1) && l.contains(p3))
    val oneLine = lines.exists(l => l.contains(p1) && l.contains(p2) && l.contains(p3))
    l1 && l2 && l3 && !oneLine
  }

  val triangles = points.combinations(3).map(_.toList).foldLeft(0) {
    case (triangles, List(p1, p2, p3)) =>
      if (isTriangle(p1, p2, p3)) {
        println("""(p1, p2, p3) = """ + (p1, p2, p3))
        triangles + 1
      } else
        triangles
  }

  println("""triangles = """ + triangles)
}
