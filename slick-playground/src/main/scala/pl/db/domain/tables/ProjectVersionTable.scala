package pl.db.domain.tables

import java.sql.Timestamp

import pl.db.domain.model.ProjectVersion
import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

class ProjectVersionTable(tag: Tag) extends Table[ProjectVersion](tag, "PROJECT_VERSION") {
  def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
  def projectId = column[Long]("PROJECT_ID")
  def releaseName = column[String]("RELEASE_NAME")
  def releaseDate = column[Timestamp]("RELEASE_DATE")
  def details = column[String]("DETAILS")

  def * = (id, projectId, releaseName, releaseDate, details) <> ((ProjectVersion.apply _).tupled, ProjectVersion.unapply)
}

