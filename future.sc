import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

var s = System.currentTimeMillis()

def c = System.currentTimeMillis() - s

def a(id: Int) = {
  println(s">$id: $c")
  Thread.sleep(200)
  println(s"<$id: $c")
  id
}

a(1)
println(s"1---------- $c")
val f = (1 to 5).map(x => Future { a(x) })
println(s"2---------- $c")
//val r = for {
//  h <- Future.sequence(f)
//  g = h.toList
//} yield {
//  println(s"3------ done $c $g")
//  g
//}
//val t = Await.result(r, Duration("5s"))

val d = f.map(x => Await.result(x, Duration("5s")))
println(s"3------ done $c $d")
