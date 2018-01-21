package pl.tracing.messaging


case class Message private(
  command: Command,
  passthrough: Option[State],
  tracingData: MessageTracingData
)

object Message {
  def createNew(command: Command, passthrough: Option[State], source: Service, target: Service) =
    Message(command, passthrough, MessageTracingData.createNew(command.name, source, target))

  def createFollowUp(command: Command, passthrough: Option[State], previousTracingData: MessageTracingData, source: Service, target: Service) =
    Message(command, passthrough, MessageTracingData.createFollowUp(previousTracingData, command.name, source, target))
}

object O extends App {

  import org.json4s._
  import org.json4s.native.Serialization


  trait S
  case class UnknownS(json: Any) extends S
  case object S1 extends S
  case object S2 extends S
  case object S3 extends S

  trait T

  case class A(s: Option[S], a1: Int, a2: Int) extends T
  case class B(s: Option[S], b1: Int, b2: Int, b3: Int) extends T
  case class C(s: Option[S], c1: Int, c2: Int, c3: Int, c4: Int) extends T


  object S1Serializer extends CustomSerializer[S](format => ( {
    case JString("S1") => S1
    case x => UnknownS(x)
  }, {
    case S1 => JString("S1")
    case UnknownS(x) => x.asInstanceOf[JValue]
  }
  ))

  object S2Serializer extends CustomSerializer[S](format => ( {
    case JString("S2") => S2
    case x => UnknownS(x)
  }, {
    case S2 => JString("S2")
    case UnknownS(x) => x.asInstanceOf[JValue]
  }
  ))

  val formats1 = DefaultFormats + S1Serializer +
    ShortTypeHints(List(classOf[A], classOf[B]))
  val formats2 = DefaultFormats + S2Serializer +
    ShortTypeHints(List(classOf[A], classOf[B]))

  val a = A(Some(S1), 1, 1)
  val b = A(Some(S1), 1, 1)
  val m1 = Serialization.write(a)(formats1)
  val m2 = Serialization.read[A](m1)(formats2, implicitly[Manifest[A]])
  val m3 = Serialization.write(m2)(formats2)
  val m4 = Serialization.read[A](m3)(formats1, implicitly[Manifest[A]])
  a == b
  a == m4
}