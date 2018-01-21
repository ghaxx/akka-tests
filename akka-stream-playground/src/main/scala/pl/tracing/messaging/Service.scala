package pl.tracing.messaging

case class Service(name: String) extends AnyVal

object Services {
  val JLS = Service("JLS")
}
