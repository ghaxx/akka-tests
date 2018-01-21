package pl.tracing.messaging

trait State

object State {
  case class UnknownState(x: Any) extends State
}
