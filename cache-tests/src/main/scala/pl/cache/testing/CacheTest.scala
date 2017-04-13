package pl.cache.testing

object CacheTest extends App {
  val n = 3000000
  val cases = List(
    new EhcacheTestCase,
    new JcsTestCase,
    new ParMapTestCase,
    new InMemoryCacheTestCase,
    new ScaffeineTestCase
  )

  cases.foreach(_.warmup())

  Stream.from(1).map(i => math.pow(10L, i).toLong).take(8).foreach {
    n =>
      println(s"Size: $n")
      val results = cases.foldLeft(Map.empty[String, List[Long]]) {
        (results, testCase) =>
          val times = (1 to 3).foldLeft(List.empty[Long]) {
            (times, _) => testCase.run(n) :: times
          }
          results.updated(testCase.name, times)
      }
      results.toList.sortBy(_._1).foreach {
        case (name, times) => println(f"  $name%10s: ${times.mkString(", ")} -> ${times.sum / times.length}")
      }
  }

}
