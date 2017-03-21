package pl.db.infrastructure

import pl.db.domain.model.{Project, ProjectVersion}
import pl.db.domain.tables.{projectTable, projectVersionTable}
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class DataFeeder {
  def apply(projectsNumber: Int, versionsPerProject: Int) = {
    val db = Database.forConfig("slick")
    try {
      val feedProjects = DBIO.seq(
        projectTable.delete,
        projectVersionTable.delete,
        projectTable ++= Project.gen(projectsNumber)
      )
      Await.result(db.run(feedProjects), 30 seconds)

      val feedVersions = for {
        projects <- db.run(projectTable.result)
      } yield {
        for (p <- projects) {
          val r = db.run(projectVersionTable ++= ProjectVersion.gen(p.id.get, versionsPerProject))
          Await.result(r, 30 seconds)
        }
      }
      Await.result(feedVersions, 60 seconds)

      val r = sqlu"""update PROJECT as p set LATEST_VERSION=(select id from PROJECT_VERSION where PROJECT_ID=p.ID and RELEASE_NAME="0.${versionsPerProject}")"""

      Await.result(db.run(r), 10 seconds)

    } finally db.close
  }
}
