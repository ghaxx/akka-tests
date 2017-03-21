package pl.db.domain.model

import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit

case class ProjectVersion(
  id: Option[Long],
  projectId: Long,
  releaseName: String,
  releaseDate: Timestamp,
  details: String
)

object ProjectVersion {

  def gen(p: Long, n: Int): Seq[ProjectVersion] = Stream.from(1).take(n).map {
    i =>
      val v = n - i + 1
      ProjectVersion(
        id = None,
        projectId = p,
        releaseName = s"0.$v",
        releaseDate = Timestamp.from(Instant.now().minus(i, ChronoUnit.DAYS)),
        details = s"Details #$v"
      )
  }

}