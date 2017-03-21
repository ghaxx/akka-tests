package pl.db.domain

import slick.jdbc.H2Profile.api._

package object tables {
  object projectTable extends TableQuery(new ProjectTable(_)) {
    def allWithLatestVersions = {
      def maxVersion(pId: Rep[Option[Long]]) =
        projectVersionTable
          .groupBy(_.projectId)
          .map {
            case (id, version) => version.map(_.releaseDate).max
          }


      for {
        p <- this
        //        v <- projectVersionTable.filter(_.projectId.? === p.id)//.take(1)
        v <- maxVersion(p.id)
        m <- projectVersionTable if m.projectId.? === p.id && m.releaseDate.? === v
      } yield (p, m)
    }

    def allWithLatestVersions2 = {
      for {
        p <- this
        m <- projectVersionTable if m.id === p.latestVersion
      } yield (p, m)
    }
  }

  val projectVersionTable = TableQuery[ProjectVersionTable]
}
