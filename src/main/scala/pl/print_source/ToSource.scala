package pl.print_source

object ToSource {
  def toSource(a: Any): String = {
    a match {
      case s: String ⇒ "\"" + s + "\""
      case m: Map[_, _] ⇒ m.map { case (k, v) ⇒ (toSource(k), toSource(v)) }.mkString("Map(", ", ", ")")
      case l: List[_] ⇒ l.map(toSource).mkString("List(", ", ", ")")
      case p: Product ⇒ p.productIterator.map(toSource).mkString(p.getClass.getName + "(", ", ", ")")
      case other ⇒ other.toString
    }
  }
}
