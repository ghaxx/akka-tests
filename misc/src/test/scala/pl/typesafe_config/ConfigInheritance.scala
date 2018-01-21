package pl.typesafe_config

import com.typesafe.config.ConfigFactory
import pl.{MyFreeSpec, MySpec}
import scala.collection.JavaConverters._

class ConfigInheritance extends MyFreeSpec {

  "Value from Application should be used" - {
    "in here" in {
      val config = ConfigFactory.load()
      config.getString("simple-value") shouldBe "A1"
      config.getString("nested.simple-value") shouldBe "A2"
      config.getString("nested.value") shouldBe "A3"
      config.getAnyRefList("nested.array").asScala shouldBe List("A1", "A2", "A3")
      config.getInt("changes") shouldBe 1
    }
  }

}
