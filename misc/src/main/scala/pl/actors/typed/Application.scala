package pl.actors.typed

import akka.actor.{ActorRef, ActorSystem, Props, TypedActor, TypedProps}
import akka.routing.RoundRobinGroup
import akka.util.Timeout

import scala.concurrent.duration._


object Application extends App {
  implicit val timeout = Timeout(5 seconds) // needed for `?` below
  val system = ActorSystem()
  import system.dispatcher

  val supervisor = system.actorOf(Props(classOf[Supervisor]), "supervisor")
  val asker = system.actorOf(Props(classOf[Asker]), "asker")
  val typed: Typed = TypedActor(system).typedActorOf(TypedProps[DefaultTyped](), "typed")
  println("path = " + TypedActor(system).getActorRefFor(typed).path.toStringWithoutAddress)

  def namedActor(): Typed = TypedActor(system).typedActorOf(TypedProps[DefaultTyped]())

  // prepare routees
  val routees: List[Typed] = List.fill(5) { namedActor() }
  val routeePaths = routees map { r =>
    TypedActor(system).getActorRefFor(r).path.toStringWithoutAddress
  }
  // prepare untyped router
  val router: ActorRef = system.actorOf(RoundRobinGroup(routeePaths).props(), "r")

  // prepare typed proxy, forwarding MethodCall messages to `router`
  val typedRouter: Typed = TypedActor(system).typedActorOf(TypedProps[DefaultTyped](), actorRef = router)

//  val routerFromPath = TypedActor(system).typedActorOf(TypedProps[DefaultTyped](), actorRef = system.actorSelection("user/r").anchor)

//  asker ! "start"
  val m = for {
    actor <- system.actorSelection("user/r").resolveOne()
    routerFromPath: Typed = TypedActor(system).typedActorOf(TypedProps[DefaultTyped](), actorRef = actor).asInstanceOf[Typed]
    m <- routerFromPath.wait(1000)
    m2 <- routerFromPath.wait(1000)
    m2 <- routerFromPath.wait(1000)
    m2 <- routerFromPath.wait(1000)
    m2 <- routerFromPath.wait(1000)
    m2 <- routerFromPath.wait(1000)
    m2 <- routerFromPath.wait(1000)
    m2 <- routerFromPath.wait(1000)
  } yield {
    println(s"inside yield done: $m2")
    m2
  }
  println(s"after yield: $m")
//  val m = for {
//    m <- akka.pattern.ask(supervisor, Supervisor.Delay(3000)).mapTo[String]
//    m <- akka.pattern.ask(supervisor, Supervisor.Delay(2500)).mapTo[String]
//    m <- akka.pattern.ask(supervisor, Supervisor.Delay(2000)).mapTo[String]
//    m <- akka.pattern.ask(supervisor, Supervisor.Delay(1500)).mapTo[String]
//    m <- akka.pattern.ask(supervisor, Supervisor.Delay(1000)).mapTo[String]
//    v <- Future{m.toString}
//  } yield {
//    println("yield done")
//    v
//  }
//  println("after yield")
//
//
//  println(Await.result(m, 20 seconds))
//  (supervisor ? Supervisor.Delay(3000)).mapTo[String].onSuccess {case v => println(v) }
//  (system.actorSelection("user/supervisor") ? Supervisor.Delay(2500)).mapTo[String].onSuccess{case v => println(v) }
//  (system.actorSelection("user/supervisor") ? Supervisor.Delay(2000)).mapTo[String].onSuccess{case v => println(v) }
//  (system.actorSelection("user/supervisor") ? Supervisor.Delay(1500)).mapTo[String].onSuccess{case v => println(v) }
//  (system.actorSelection("user/supervisor") ? Supervisor.Delay(1000)).mapTo[String].onSuccess{case v => println(v) }
}
