package pl.db.domain.tables

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}
import pl.db.UnitSpec
import pl.db.domain.model.{Project, ProjectVersion}
import pl.db.infrastructure.DataFeeder
import pl.performance.Timer
import slick.dbio.Effect.Read
import slick.sql.FixedSqlStreamingAction

import scala.concurrent.duration._

class ProjectTableQuerySpec extends UnitSpec {

  import slick.jdbc.H2Profile.api._

  val numberOfProjects = 2
  val numberOfVersions = 4
  val lastVersion = s"0.$numberOfVersions"


  it("should feed") {
    new DataFeeder().purgeData()
    val e = Timer.measure {
      new DataFeeder().feedAndBindLater(numberOfProjects, numberOfVersions)
    }
    info(s"it took $e")
  }

  it("should feed and update with each row using SQL") {
    new DataFeeder().purgeData()
    val e = Timer.measure {
      new DataFeeder().feed(numberOfProjects, numberOfVersions)
    }
    info(s"it took $e")
  }

  it("should feed and update with each row using Slick") {
    new DataFeeder().purgeData()
    val e = Timer.measure {
      new DataFeeder().feedSlick(numberOfProjects, numberOfVersions)
    }
    info(s"it took $e")
  }

  it("should feed and update with each row using Slick cpl") {
    new DataFeeder().purgeData()
    val e = Timer.measure {
      new DataFeeder().feedSlickAndBindLater(numberOfProjects, numberOfVersions)(null)
    }
    info(s"it took $e")
  }

  it("Should be quick to get with index") {
    val db = Database.forConfig("slick")

    val (w, action) = Timer.measureAndReturn {
      val action = ProjectTableQuery.allWithLatestVersionsByGrouping.result
      action.statements.foreach(println)
      action
    }
    info(s"it was prepared in $w")
    val e = Timer.measure {
      val result = db.run(action).futureValue
      result.foreach {
        case (p, v) => assert(lastVersion == v.releaseName)
      }
      assert(result.size == numberOfProjects)
    }
    info(s"it executed in $e")
  }

  it("Should be quick to get by id") {
    val db = Database.forConfig("slick")

    val (w, action) = Timer.measureAndReturn {
      val action = ProjectTableQuery.allWithLatestVersionsByRef.result
      action.statements.foreach(println)
      action
    }
    info(s"it was prepared in $w")
    val e = Timer.measure {
      val result = db.run(action).futureValue
      result.foreach {
        case (p, v) => assert(lastVersion == v.releaseName)
      }
      assert(result.size == numberOfProjects)
    }
    info(s"it executed in $e")
  }

  override implicit def patienceConfig = PatienceConfig(300 seconds, 1 second)
}
