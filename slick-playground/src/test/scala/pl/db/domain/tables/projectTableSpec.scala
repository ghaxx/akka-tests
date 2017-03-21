package pl.db.domain.tables

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}
import pl.db.infrastructure.DataFeeder
import pl.performance.Timer

import scala.concurrent.duration._

class projectTableSpec extends FunSpec with ScalaFutures with Matchers {

  import slick.jdbc.H2Profile.api._

  val d = 500

  it("should feed") {
    new DataFeeder().apply(d, 50)
  }

  it("Should be quick") {
    val db = Database.forConfig("slick")

    val e = Timer.elapsed {
      val result = projectTable.allWithLatestVersions.result
      result.statements.foreach(println)
      val r = db.run(result).futureValue
      r.foreach {
        case (p, v) => assert(".50" == v.releaseName.substring(v.releaseName.length - 3))
      }
      assert(r.size == d)
    }
    info(s"it took $e")
    assert(e <= patienceConfig.timeout.millisPart)
  }

  it("Should be quick too") {
    val db = Database.forConfig("slick")

    val e = Timer.elapsed {
      val result = projectTable.allWithLatestVersions2.result
      result.statements.foreach(println)
      val r = db.run(result).futureValue
      r.foreach {
        case (p, v) => assert(".50" == v.releaseName.substring(v.releaseName.length - 3))
      }
      assert(r.size == d)
    }
    info(s"it took $e")
    assert(e <= patienceConfig.timeout.millisPart)
  }

  override implicit def patienceConfig = PatienceConfig(300 seconds, 1 second)
}
