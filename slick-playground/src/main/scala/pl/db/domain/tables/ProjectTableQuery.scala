package pl.db.domain.tables

import slick.jdbc.H2Profile.api._
import slick.lifted.{Rep, TableQuery}

object ProjectTableQuery extends TableQuery(new ProjectTable(_)) {
  def allWithLatestVersionsByGrouping =  {
    def maxVersion =
      ProjectVersionTableQuery
        .groupBy(_.projectId)
        .map {
          case (id, version) => version.map(_.releaseDate).max
        }

    for {
      p <- this
      v <- maxVersion
      m <- ProjectVersionTableQuery if m.projectId.? === p.id && m.releaseDate.? === v
    } yield (p, m)
  }

  def allWithLatestVersionsByRef =  {
    for {
      p <- this
      m <- ProjectVersionTableQuery if m.id === p.latestVersionId
    } yield (p, m)
  }
}
