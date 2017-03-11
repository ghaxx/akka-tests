package pl.repository

import scala.collection.mutable

class MemoryStorage[Entity] extends Storage[Entity] {
  val memory = mutable.Map.empty[Int, Entity]

  def get(id: Int): Entity = memory(id)
  def put[W](id: Int, value: W)(implicit ev: Persistable[W, Entity]): W = {
    memory.put(id, ev.getObject(value))
    value
  }
}

