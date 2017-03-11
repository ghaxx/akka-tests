package pl.repository

trait Storage[A] {

  implicit val identity = Storage.IdentityPersistable

  def get(id: Int): A

  def put[W](id: Int, value: W)(implicit ev: Persistable[W, A]): W

}

object Storage {
  implicit object IdentityPersistable extends Persistable[String, String] {
    def getObject(w: String): String = w
  }
}