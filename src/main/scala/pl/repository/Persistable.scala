package pl.repository

trait Persistable[Wrapper, Entity] {

  def getObject(w: Wrapper): Entity

}
