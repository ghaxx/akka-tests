package pl.db.domain.tables

import pl.db.domain.model.Project
import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

class ProjectTable(tag: Tag) extends Table[Project](tag, "PROJECT") {
  def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")
  def author = column[String]("AUTHOR")
  def description = column[String]("DESCRIPTION")
  def latestVersion = column[Option[Long]]("LATEST_VERSION")

  def * = (id, name, author, description, latestVersion) <> ((Project.apply _).tupled, Project.unapply)


}

