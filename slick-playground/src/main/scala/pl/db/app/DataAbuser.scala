package pl.db.app

import pl.db.domain.model.Project
import pl.db.domain.tables.{ProjectTableQuery, ProjectVersionTableQuery}
import pl.db.infrastructure.DataFeeder

import scala.concurrent.{Await, Future}
import slick.jdbc.H2Profile.api._

object DataAbuser extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._
  implicit val db = Database.forConfig("slick")
  val feeder = new DataFeeder()
//  try {
//    feeder.purgeData()
//  } finally {}

  def workers = (1 to 4).toList.map { i =>
    Future {
      while (true) {
        try {
//          Await.result(db.run(ProjectTableQuery ++= Project.gen(100)), 5 seconds)
          feeder.feedSlickAndBindLater(2, 2)
//          println(s"$i inserted")
        } catch {
          case t: Throwable => println(t.getMessage)
        }
      }
    }
  }
  def counter = Future {
    while (true) {
      try {
        feeder.createSchema
      } catch {
        case _: Throwable => // ignored
      }
      try {
        val size = Await.result(db.run(ProjectTableQuery.length.result), 5 seconds)
        val sizeV = Await.result(db.run(ProjectVersionTableQuery.length.result), 5 seconds)
        println(s"size $size, v $sizeV")
        Thread.sleep(2000)
      } catch {
        case t: Throwable => println(t.getMessage)
      }
    }
  }

  Await.result(Future.sequence(counter :: workers), Duration.Inf)

}
