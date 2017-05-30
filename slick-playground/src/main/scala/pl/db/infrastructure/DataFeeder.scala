package pl.db.infrastructure

import pl.db.domain.model.{Project, ProjectVersion}
import pl.db.domain.tables.{ProjectTableQuery, ProjectVersionTableQuery}
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class DataFeeder {

  val to = 300 seconds

  def createSchema() = {
    val db = Database.forConfig("slick")
    try {
      val createTables = DBIO.seq(
        ProjectTableQuery.schema.create,
        ProjectVersionTableQuery.schema.create
      )
      Await.result(db.run(createTables), to)
    } finally {
      db.close()
    }
  }

  def feedAndBindLater(projectsNumber: Int, versionsPerProject: Int) = {
//    purgeData()
    val db = Database.forConfig("slick")
    try {
      Await.result(db.run(ProjectTableQuery ++= Project.gen(projectsNumber)), to)

      val feedVersions = for {
        projects <- db.run(ProjectTableQuery.result)
      } yield {
        for (p <- projects) {
          val r = db.run(ProjectVersionTableQuery ++= ProjectVersion.gen(p.id.get, versionsPerProject))
          Await.result(r, to)
        }
      }
      Await.result(feedVersions, to)

      val r = sqlu"""update PROJECT as p set LATEST_VERSION=(select id from PROJECT_VERSION where PROJECT_ID=p.ID and RELEASE_NAME=${"0."+versionsPerProject})"""

      Await.result(db.run(r), to)

    } finally db.close
  }
  def feed(projectsNumber: Int, versionsPerProject: Int) = {
//    purgeData()
    val db = Database.forConfig("slick")
    try {
      Await.result(db.run(ProjectTableQuery ++= Project.gen(projectsNumber)), to)

      val feedVersions = for {
        projects <- db.run(ProjectTableQuery.result)
      } yield {
        for (p <- projects) {
          val s = ProjectVersion.gen(p.id.get, versionsPerProject).map {
            v => ProjectVersionTableQuery.insertSlick(v)
          }
          val r = db.run(DBIO.seq(s: _*))
          Await.result(r, to)
        }
      }
      Await.result(feedVersions, to)

    } finally db.close
  }
  def feedSlick(projectsNumber: Int, versionsPerProject: Int) = {
//    purgeData()
    val db = Database.forConfig("slick")
    try {
      Await.result(db.run(ProjectTableQuery ++= Project.gen(projectsNumber)), to)

      val feedVersions = for {
        projects <- db.run(ProjectTableQuery.result)
      } yield {
        for (p <- projects) {
          val s = ProjectVersion.gen(p.id.get, versionsPerProject).map {
            v => ProjectVersionTableQuery.insertSlick(v)
          }
          val r = db.run(DBIO.seq(s: _*))
          Await.result(r, to)
        }
      }
      Await.result(feedVersions, to)

    } finally db.close
  }
  def feedSlickAndBindLater(projectsNumber: Int, versionsPerProject: Int)(implicit db: H2Profile.backend.Database) = {
//    purgeData()
//    val db = Database.forConfig("slick")
    try {
      Await.result(db.run((ProjectTableQuery ++= Project.gen(projectsNumber))), to)

      val feedVersions = for {
        projects <- db.run(ProjectTableQuery.result)
      } yield {
        for (p <- projects) {
          val s = ProjectVersion.gen(p.id.get, versionsPerProject).map {
            v => ProjectVersionTableQuery.insertSlickCpl(v)
          }
          val seq = DBIO.seq(s: _*)
          val r = db.run(seq)
          Await.result(r, to)
        }
      }
      Await.result(feedVersions, to)

    } finally {
//      db.close
    }
  }

  def purgeData() = {
    val db = Database.forConfig("slick")
    try {
      val feedProjects = DBIO.seq(
        ProjectTableQuery.delete,
        ProjectVersionTableQuery.delete,
        sqlu"""ALTER TABLE PROJECT ALTER COLUMN ID RESTART WITH 1""",
        sqlu"""ALTER TABLE PROJECT_VERSION ALTER COLUMN ID RESTART WITH 1"""
      )
      Await.result(db.run(feedProjects), to)
    } finally db.close
  }
}
