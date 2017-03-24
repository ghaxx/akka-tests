package pl.db.domain.model

case class Project(
  id: Option[Long],
  name: String,
  author: String,
  description: String,
  latestVersionId: Option[Long]
)

object Project {

  def gen(n: Int): Seq[Project] = Stream.from(1).take(n).map {
    i =>
      Project(
        id = None,
        name = s"Project #$i",
        author = s"Author #$i",
        description = s"Description #$i",
        latestVersionId = None
      )
  }

}