package pl.db.domain.tables

import slick.jdbc.H2Profile.api._
import pl.db.domain.model.ProjectVersion
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global

object ProjectVersionTableQuery extends TableQuery(new ProjectVersionTable(_)) {
  def insert(version: ProjectVersion) = {
    for {
      inserted <- forInsert += version
      _ <- sqlu"""update PROJECT set LATEST_VERSION=${inserted.id.get} where ID=${inserted.projectId} and (LATEST_VERSION is null or ${inserted.releaseDate}>=(select RELEASE_DATE from PROJECT_VERSION where ID=PROJECT.LATEST_VERSION))"""
    } yield {
      inserted
    }
  }

  def insertSlick(version: ProjectVersion) = {
    def updateVersion(v: ProjectVersion) = {
      val lv = for {
        p <- ProjectTableQuery if p.id === v.projectId
        oldV <- ProjectVersionTableQuery.filter(_.id === p.latestVersionId)
        if p.latestVersionId.isEmpty || oldV.releaseDate <= v.releaseDate
      } yield p.latestVersionId
      lv.update(v.id).statements.foreach(println)
      lv.update(v.id)
    }

    for {
      inserted <- forInsert += version
      _ <- updateVersion(inserted)
    } yield {
      inserted
    }
  }
  def insertSlickCpl(version: ProjectVersion) = {
    def maxDate(id: Rep[Option[Long]]) =
      ProjectVersionTableQuery
        .filter(_.projectId === id)
        .map(_.releaseDate)
        .max

    def updateVersion(v: ProjectVersion) = {
      val lv = for {
        p <- ProjectTableQuery if p.id === v.projectId && (p.latestVersionId.isEmpty || maxDate(p.id) <= v.releaseDate)
      } yield p.latestVersionId
      lv.update(v.id)
    }

    for {
      inserted <- forInsert += version
      _ <- updateVersion(inserted)
    } yield {
      inserted
    }
  }

  val forInsert = {
    this returning this.map(_.id) into { (version, id) =>
      version.copy(id = id)
    }
  }
}
