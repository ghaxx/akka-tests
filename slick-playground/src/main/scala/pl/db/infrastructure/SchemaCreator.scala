package pl.db.infrastructure

import pl.db.domain.tables.{ProjectTableQuery, ProjectVersionTableQuery}
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

object SchemaCreator extends App {
  val db = Database.forConfig("slick")
  try {
    try {
      val drop =
        (ProjectTableQuery.schema ++ ProjectVersionTableQuery.schema).drop
      Await.result(db.run(drop), 30 seconds)
    } catch {
      case e: Throwable => println(e.getMessage)
    }
    try {
      val create =
        (ProjectTableQuery.schema ++ ProjectVersionTableQuery.schema).create
      Await.result(db.run(create), 30 seconds)
    } catch {
      case e: Throwable => println(e.getMessage)
    }
  } finally db.close
}
