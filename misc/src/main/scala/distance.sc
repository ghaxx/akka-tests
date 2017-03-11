import scala.util.Random
import Math._

sqrt(pow(0 - 0.5, 2) + pow(0 - 0.5, 2))
2^4
val d = for {
  i ← 1 to 100000
  (x1, y1) = (Random.nextDouble(), Random.nextDouble())
  (x2, y2) = (Random.nextDouble(), Random.nextDouble())
} yield sqrt(pow(x1 - x2, 2) + pow(y1 - y2, 2))

d.sum / d.length