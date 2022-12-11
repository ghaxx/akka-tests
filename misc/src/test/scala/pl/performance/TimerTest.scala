//package pl.performance
//
//import pl.MyFunSpec
//
//class TimerTest extends MyFunSpec {
//
//  test("Timer should start when created") {
//    val t = Timer("test")
//    Thread.sleep(200)
//    val measuredTime = t.elapsed
//    measuredTime should be >= 200L
//    measuredTime should be <= 210L
//  }
//
//  test("Checking elapsed time should yield correct results since timer creation") {
//    val t = Timer("test")
//    Thread.sleep(100)
//    val firstMeasure = t.elapsed
//    Thread.sleep(100)
//    val secondMeasure = t.elapsed
//
//    firstMeasure should be >= 100L
//    firstMeasure should be <= 110L
//    secondMeasure should be >= 200L
//    secondMeasure should be <= 210L
//  }
//
//  test("Timer should correctly reset") {
//    val t = Timer("test")
//    Thread.sleep(200)
//    val measuredTime = t.elapsed
//    measuredTime should be >= 200L
//    measuredTime should be <= 210L
//
//    t.reset()
//    Thread.sleep(100)
//    val secondMeasure = t.elapsed
//    Thread.sleep(200)
//    val thirdMeasure = t.elapsed
//    secondMeasure should be >= 100L
//    secondMeasure should be <= 110L
//    thirdMeasure should be >= 300L
//    thirdMeasure should be <= 310L
//  }
//
//  test("Timer should correctly pause") {
//    val t = Timer("test")
//    Thread.sleep(200)
//    t.pause()
//    Thread.sleep(200)
//    val measuredTime = t.elapsed
//    measuredTime should be >= 200L
//    measuredTime should be <= 210L
//  }
//
//  test("Timer should correctly unpause") {
//    val t = Timer("test")
//    Thread.sleep(200)
//    t.pause()
//    Thread.sleep(200)
//    t.start()
//    Thread.sleep(200)
//    val measuredTime = t.elapsed
//    measuredTime should be >= 400L
//    measuredTime should be <= 410L
//  }
//
//}
