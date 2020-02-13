#Scala Job Interview Questions

#### General Questions:
[] Test
* What did you learn yesterday/this week? 
* Why and how did you start learning Scala?
* What excites or interests you about coding in Scala?
* What is a recent technical challenge you experienced and how did you solve it?
* Talk about your preferred development environment. (OS, Editor or IDE, Tools, etc.)
* What are your thoughts about the other JVM languages compared to Scala?
* Do you think that the Scala language and community is mature enough?

#### Language Questions:

* What is the difference between a `var`, a `val` and `def`? 
    * How `lazy val` works? How does it behave when overriding?
* How many argument can a function, constructor have? (should be unlimited now)
* What is the difference between a `trait` and an `abstract class`? 
* What is the difference between an `object` and a `class`?
* What is a `case class`? 
    * What is trait `Product`? 
    * What are methods `productPrefix` and `productIterator`?
    * Does creating identical case classes impact memory? (Are they internalized like strings?)
* What is the difference between a Java future and a Scala future?
* What is the difference between `unapply` and `apply`, when would you use them?
    * Regexp?
* What are *value classes*? (extends `AnyVal`)
    * What are limitations of those objects? 
* What is a companion object?
* What is the difference between `sealed` and `final`? Can those be combined?
* What is the difference between the following terms and types in Scala: `Nil`, `Null`, `None`, `Nothing`?
* What is `Unit`?
    * Is there any danger related to using `Future[Unit]`?
* What is the difference between a `call-by-value` and `call-by-name` parameter? 
	* How does Scala's `Stream` trait levarages `call-by-name`?
* Define uses for the `Option` monad and good practices it provides.
* How does `yield` work?
* Explain the implicit parameter precedence.
* What operations is a `for comprehension` syntactic sugar for?
* Streams:
	* What consideration you need to have when you use Scala's `Streams`? 
	* What technique does the Scala's `Streams` use internally? 
* What is `lazy val`? 
    * Is it thread safe? 
    * What problems can be caused by using `lazy val`s? (Deadlocks) 
* Can you override operators?
    * What's the operator precedense?
    * What's the role of colon? What's the difference between `:+` and `+:`?
        * What will be the result of calling methods of this class?
        ```scala
        object A {
          def :+(s: String): String = s"A.:+($s)"
          def +:(s: String): String = s"A.+:($s)"
        }
        
        "a" +: A // res1: String = A.+:(a)
        A :+ "a" // res0: String = A.:+(a)
        A +: "a" // res2: scala.collection.immutable.IndexedSeq[Any] = Vector(A$A293$A$A293$A$@37baa95a, a)
    
        ````
* Pattern matching a regexp?
* How variance works in scala? 
    * Is scala `Array` covariant? Why? (no)
    * (Bonus) Difference between view bounds? (parameterized type required)
    * Will it compile? (no) 
    ```scala
    trait F[+A, -B] {
      def f(a: A): B
    }
    ```
    * How can you use covariant parameter as a function argument type?
        * Answer:
        ```scala
        trait F[+A, -B] {
          def f[AA >: A, BB <: B](a: AA): BB
        }
        ```
* What are implicit parameters?
* What are implicit functions?
* What are implicit classes?


#### Functional Programming Questions:

* What is a `functor`?
* What is a `applicative`?
    * Where are they used? 
        * Scalaz Validation
        * Scalaz traverse
* What is a `monad`? 
  * What are the `monad` axioms? 
  * What Scala data types are, or behave like, monads?
  * What are the basic and optional requirement/s to conform a Monad?
  * Is `Future`, `Try`, `Option` a monad? 
* Explain higher order functions.
* What is gained from using immutable objects?
* What is tail recursion? 
  * How does it differentiate from common recursion?
  * What issues are there with tail recursive functions in the JVM? (there's none in JVM)
  * How does the Scala compiler optimize a tail recursive function?
  * How do you ensure that the compiler optimizes the tail recursive function?
  * Can you tell what will be the result? 
  ```scala
   def sum(n: Int): Int = n match {
     case 1 => 1
     case n => n + sum(n - 1)
   }
   sum(10)
   sum(100000) // java.lang.StackOverflowError
  ```
  * How to fix above example?
  ```scala
  def sum(n: Long): Long = {
    def __sum(current: Long, n: Long): Long = n match {
      case 0 => current
      case n => __sum(current + n, n - 1)
    }
  
    __sum(0, n)
  }
  sum(100000)
  ```
* What is function currying?
    * Mention implicit value classes.
* What is the difference between implicit functions and implicit classes? 
* What are typeclasses? 
* What are lenses?
* What is and which are the uses of: Enumerators, Enumeratees and Iteratee

#### Scala Programming Questions:

* How can you make a `List[T]` from a `List[List[T]]`? 
    * flatten
    * flatMap
    * fold
    * for-comprehension
* What's the difference between `fold` and `reduce`?
* What is the difference (if any) between these 2 statements? 
```scala
var x = scala.collection.Set.empty[Int]
val y = scala.collection.mutable.Set.empty[Int]
```
* How would you compose multiple monads of the same type (especially `Future`s)?
    * What's the difference between `Future.sequence` and `foldLeft`? 
     ```scala
     import scala.concurrent.{Await, Future}
     import scala.concurrent.duration._
     import scala.concurrent.ExecutionContext.Implicits.global
     
     def f(i: Int): Future[Int] = Future {
       Thread.sleep(i)
       i
     }
     
     def l = List(2000, 1000, 3000)
     
     val r1 = Future.sequence(l.map(f))
     Await.result(r1, 4 seconds)
     
     val r2 = l.foldLeft(Future.successful(List.empty[Int])) {
       (memo, elem) => 
         for {
           m <- memo
           i <- f(elem)
         } yield i :: m 
     }
     Await.result(r2, 7 seconds)
     ```
* How can you make a `Future[List[T]]` from a `List[Future[T]]`? 
    * Future.sequence
* Is this call correct?
    * How log would it take to complete? 
    * Could it be quicker? 
```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

def f(i: Int): Future[Int] = Future {
  Thread.sleep(i)
  i
}

for {
  f1 <- f(1000)
  f2 <- f(2000)
} yield f1 + f2
```
* Could the following code be improved like the previous one?
```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

def f(i: Int): Future[Int] = Future {
  Thread.sleep(i)
  i
}

for {
  f1 <- f(1000)
  f2 <- f(f1 + 2000)
} yield f1 + f2
```
* How would you combine different monads? 
```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

for {
  f1 <- Option(1000)
  f2 <- Future(2000)
} yield f1 + f2
```
* How do you handle exceptions? 
    * Do you use `Either`, `Xor`, `\/`? 
* Is it possible to override already defined type? (no)

#### Reactive Programming Questions:

* Explain the actor model.
* What are benefits of non-blocking (asynchronous I/O) over blocking (synchronous I/O).
* Do you think that Scala has the same async spirit as Node.js?
* Explain the difference between `concurrency` and `parallelism`, and name some constructs you can use in Scala to leverage both.
* What is the global ExecutionContext? 
  * What does the global ExecutionContext underlay? 
* Akka:
	* Which are the 3 main components in a Stream?
* Do you know some libraries doing streams? 

##### Couchbase:

* Why a database would use asynchronous access (RX)? 
* Concurrent writes. 
* Partial updates.
* Unique values.

##### Akka:

* What is `ActorSystem`?
    * Is is possible to have multiple systems in one app?
* What is a dispatcher?
    * Is it possible to have multiple dispatchers in one app or system?
* What is a materializer?
* (Bonus) How to cause stream deadlock?
* (Bonus) Dangers of reactive-kafka library?

##### Kafka:

* What is Kafka? 
* How Kafka handles messages? 
* What is kafka delivery contract: exactly/at least/at most once? 
* Where is offset stored? 
* Can you read from custom offset? 
* What is partitioning? 
    * What could be a use case for partitions?
* What is Kafka cluster? 
* What is zookeeper used for?

##### JVM:

* How to find hanging thread? 
* How would you approach issue of a server dying after few days after every restart? 

##### SQL:

* What's the difference between `where` and `having`?
* Why use indexes?

##### REST:

* What are HTTP methods? 
* Do you know REST levels (Richardson Maturity Model)?
* What's the difference between PATCH and PUT? 
    * How does PATCH work? (https://tools.ietf.org/html/rfc5789#section-2.1)
    * What's JSON Merge Patch? (https://tools.ietf.org/html/rfc6902#appendix-A.1, https://tools.ietf.org/html/rfc7396)
    * What's JSON Patch document? 

##### ScalaZ:

* What is `Either`/`Disjunction`? (right biased either, deprecated in 2.12)
    * What is `EitherT`? (e.g. transforms Future\[A\\/B\] to B in for-comprehension)
* What is `Validation`?
* What is `.traverse`/`.traverseU`?
* Do you like ‘scalaz‘? 

##### Shapeless:

* What is `HList`? 
* What is `KList`? 
* What is `Coproduct`? 
* What would be a valid use case for shapeless? (abstraction over arity: urls in akka-http, lambdas with arbitrary number of arguments)

##### Other:

* What is CAP theorem? 
* What is event sourcing? 
* What is DDD? 
* What is TDD? (Maybe type driven development?)

#### "Other" Questions:

* What's the result of following code? (2, not 3 because no braces)
```scala
if (false)
  println(1)
  if (true)
    println(2)
else
  println(3)
```
* What's a cool project that you've recently worked on?
* What testing framework for Scala do you use?
    * Are you familiar with table/property based tests? What's the difference?
    * What is TDD, ATDD (acceptance)?
* What do you know about property based testing frameworks, such as Scalacheck? 
* Do you like ‘scala.js‘? 
