//package pl.fsm
//
//import akka.actor.FSM.{Normal, Reason}
//import akka.actor.NoSerializationVerificationNeeded
//
//import scala.collection.mutable
//import scala.concurrent.Future
//import scala.concurrent.duration.{Duration, FiniteDuration}
//
//trait FSMMy[S, D] {
//
//  type State = FSMMy.State[S, D]
//  type Event = FSMMy.Event[D]
//  type StateFunction = scala.PartialFunction[Event, State]
//  type Timeout = Option[FiniteDuration]
//
//
//  val Event: FSMMy.Event.type = FSMMy.Event
//  val -> = FSMMy.->
//
//  def self = this
//
//  def !(event: Any) = {
//    stateFunctions(currentState.stateName)(Event(event, currentState.stateData))
//  }
//
//  final def startWith(stateName: S, stateData: D, timeout: Timeout = None): Unit =
//    currentState = FSMMy.State(stateName, stateData, timeout)
//
//  final def when(stateName: S, stateTimeout: FiniteDuration = null)(stateFunction: StateFunction): Unit =
//    register(stateName, stateFunction, Option(stateTimeout))
//
//  final def goto(nextStateName: S): State = FSMMy.State(nextStateName, currentState.stateData)
//  final def stay(): State = goto(currentState.stateName) // cannot directly use currentState because of the timeout field
//  final def stop(): State = stop(Normal)
//  final def stop(reason: Reason): State = stop(reason, currentState.stateData)
//  final def stop(reason: Reason, stateData: D): State = stay using stateData withStopReason (reason)
//
//  final def result: Future[D] = Future {
//    currentState.stateData
//  }
//
//  private var currentState: State = _
//  private var nextState: State = _
//  private val stateFunctions = mutable.Map[S, StateFunction]()
//  private val stateTimeouts = mutable.Map[S, Timeout]()
//
//  private def register(name: S, function: StateFunction, timeout: Timeout): Unit = {
//    if (stateFunctions contains name) {
//      stateFunctions(name) = stateFunctions(name) orElse function
//      stateTimeouts(name) = timeout orElse stateTimeouts(name)
//    } else {
//      stateFunctions(name) = function
//      stateTimeouts(name) = timeout
//    }
//  }
//
//  final def nextStateData = nextState match {
//    case null ⇒ throw new IllegalStateException("nextStateData is only available during onTransition")
//    case x    ⇒ x.stateData
//  }
//
//
//  final def onTransition(transitionHandler: TransitionHandler): Unit = transitionEvent :+= transitionHandler
//}
//
//object FSMMy {
//
//  object -> {
//    def unapply[S](in: (S, S)) = Some(in)
//  }
//
//  case class State[S, D](stateName: S, stateData: D, timeout: Option[FiniteDuration] = None, stopReason: Option[Reason] = None) {
//
//    def forMax(timeout: Duration): State[S, D] = timeout match {
//      case f: FiniteDuration ⇒ copy(timeout = Some(f))
//      case _                 ⇒ copy(timeout = None)
//    }
//
//    def using(nextStateDate: D): State[S, D] = {
//      copy(stateData = nextStateDate)
//    }
//
//    def withStopReason(reason: Reason): State[S, D] = {
//      copy(stopReason = Some(reason))
//    }
//  }
//
//  case class Event[D](event: Any, stateData: D) extends NoSerializationVerificationNeeded
//
//  case class StopEvent[S, D](currentState: S, stateData: D) extends NoSerializationVerificationNeeded
//
//}
