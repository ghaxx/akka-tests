package pl.db.infrastructure

import pl.db.domain.tables.{projectTable, projectVersionTable}
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object SchemaCreator extends App {
  val db = Database.forConfig("slick")
  try {
    val setup = DBIO.seq(
      (projectTable.schema ++ projectVersionTable.schema).drop,
      (projectTable.schema ++ projectVersionTable.schema).create
    )
    Await.result(db.run(setup), 30 seconds)
  } finally db.close
}
