package pl.repository

class InMemoryRepository {
  val storage = new MemoryStorage[String]

  import Storage._

  implicit object AnyPers extends Persistable[Any, String] {
    def getObject(w: Any): String = w.toString
  }

  def get(id: Int): String = storage.get(id)
  def put(id: Int, value: String): String = storage.put(id, value)
  def put(id: Int, value: Any): Any = storage.put(id, value)
}
