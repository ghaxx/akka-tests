package pl.probability

import java.rmi.activation.ActivationSystem

import scala.util.Random

object CoinToss extends App {
  val tries = 10000000
  val throws = 100
  val heads = 50

  var timeMarker = System.currentTimeMillis()
  val throwsResults = (1 to tries).fold(0) { (successes, i) =>
    if (System.currentTimeMillis() - timeMarker > 5000) {
      println(s"progress: $i / $tries (${100*i/tries}%)")
      timeMarker = System.currentTimeMillis()
    }
    val coinsLandings = (1 to throws).fold(0)((sum, _) => sum + Random.nextInt(2))
//    println("""coinsLandings = """ + coinsLandings.mkString(""))
    if (coinsLandings == heads) successes + 1
    else successes
  }
  val successes = throwsResults
  val probability = 1.0 * successes / tries

  println("""successes = """ + successes)
  println("""probability = """ + probability)
}
