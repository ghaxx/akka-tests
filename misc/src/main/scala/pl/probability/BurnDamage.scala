package pl.probability

import scala.util.Random

object BurnDamage extends App {

  trait Burn {
    def damage: (Int, Burn)
  }
  object Burn {
    val active: Burn = Active

    case object Inactive extends Burn {
      def damage: (Int, Burn) = (0, Inactive)
    }
    case object Active extends Burn {
      def damage: (Int, Burn) = {
        Random.nextInt(6) match {
          case x if x < 2 => (0, Inactive) // 1-2
          case x if x < 4 => (1, Active) // 3-4
          case x if x < 6 => (1, Active) // 5-6
        }
      }
    }
  }

  val maxTurnsToCheck = 10000
  var turns = 1

  while (turns < maxTurnsToCheck) {
    var sum = 0
    val tries = 500000
    var c = 0
    var t = 0
    val results = (1 to tries)
      .map { _ =>
        val result = (1 to turns).foldLeft((0.0, Burn.active)) {
          case ((sum, burn), _) =>
            val (dmg, newBurn) = burn.damage
            (sum + dmg, newBurn)
        }
        val totalDmg = result._1
        totalDmg
      }
      .sorted
    val averageDmg = results.sum / tries
    val meanDmg = results(tries/2)
    val maxDmg = results.last
    val top10Percentile = {
      val slice = results.slice(math.floor(tries * 0.9).toInt, tries)
      slice.sum / slice.size
    }
    val top20Percentile = {
      val slice = results.slice(math.floor(tries * 0.8).toInt, tries)
      slice.sum / slice.size
    }
    println(s"$turns turns -> avg is $averageDmg, mean is $meanDmg, max is $maxDmg, max 10% avg is $top10Percentile, max 20% avg is $top20Percentile")

    val alt = (1 to turns).map(_.toDouble).map(i => math.pow(2.0/3.0, i)).foldLeft(0.0)(_ + _)
    println(s"$turns turns -> avg is $alt")

    turns = turns match {
      case x if x < 40 => x + 1
      case x if x < 200 => x + 10
      case x if x < 2000 => x + 100
      case x => x + 1000
    }
  }

}
