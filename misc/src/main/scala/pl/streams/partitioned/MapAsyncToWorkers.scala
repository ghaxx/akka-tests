package pl.streams.partitioned

import akka.stream.ActorAttributes.SupervisionStrategy
import akka.stream._
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

final case class MapAsyncToWorkers[In, Out, Id](parallelism: Int, f: In => Future[Out], partitionKey: In => Id, maxBufferSize: Int = 1)(implicit executionContext: ExecutionContext) extends GraphStage[FlowShape[In, Out]] {

  private val in = Inlet[In]("in")
  private val out = Outlet[Out]("out")

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {

    val buffer = new mutable.Queue[In]()

    val outBuffer = new mutable.Queue[Out]()

    val state = new BoundedMap[Id, Future[Out]](parallelism)

    override def toString = s"MapAsyncToWorkers.Logic(inbuffer=$buffer, outbuffer=$outBuffer, state=$state)"

    def logState = toString

    def isClosable: Boolean = buffer.isEmpty && state.isEmpty && outBuffer.isEmpty

    def popOrPull: Unit = {
      if (buffer.isEmpty && !hasBeenPulled(in)) {
        tryPull(in)
      } else {
        tryToEmptyInBuffer
      }
      if (!state.isFull && !hasBeenPulled(in)) {
        tryPull(in)
      }
    }

    def tryToEmptyInBuffer: Unit = {
      if (buffer.nonEmpty) {
        val tmpBuffer = new mutable.Queue[In]
        while (buffer.nonEmpty) {
          tmpBuffer.enqueue(buffer.dequeue())
        }
        while (tmpBuffer.nonEmpty) {
          handleElement(tmpBuffer.dequeue)
        }
      }
    }

    def handleElement(element: In): Unit = {
      try {
        val id = partitionKey(element)
        if (state.contains(id)) {

          enqueueForLater(element)
        } else {
          val future = f(element)
          state +=(id, future)
          future.onComplete(futureHandler(id))(executionContext)
        }
      } catch {
        case NonFatal(ex) => if (decider(ex) == Supervision.Stop) failStage(ex)
      }
    }

    def failOrPull(ex: Throwable) =
      if (decider(ex) == Supervision.Stop) failStage(ex)
      else if (isClosed(in) && isClosable) completeStage()
      else if (!hasBeenPulled(in)) popOrPull

    val decider = {
      val o = inheritedAttributes.getAttribute(classOf[SupervisionStrategy], SupervisionStrategy(Supervision.resumingDecider))
//      o.map(x ⇒ x.decider).getOrElse(Supervision.resumingDecider)
      o.decider
    }

    def futureHandler(id: Id) = getAsyncCallback((result: Try[Out]) => {
      state -= id
      result match {
        case Failure(ex) =>
          failOrPull(ex)
        case Success(elem) =>
          if (elem == null) {
            val ex = new NullPointerException("Element must not be null, rule 2.13")
            failOrPull(ex)
          } else if (isAvailable(out)) {
            push(out, elem)
            popOrPull
          } else {
            outBuffer.enqueue(elem)
          }
      }
    }).invoke _

    setHandler(in, new InHandler {
      override def onPush(): Unit = {
        tryToEmptyInBuffer
        if (!state.isFull) {
          handleElement(grab(in))
        }
        if (!state.isFull && buffer.size < maxBufferSize) {
          popOrPull
        }
      }

      override def onUpstreamFinish(): Unit = {
        if (isClosable) completeStage()
      }
    })

    setHandler(out, new OutHandler {
      override def onPull(): Unit = {
        if (outBuffer.nonEmpty) push(out, outBuffer.dequeue())
        else if (isClosed(in) && isClosable) completeStage()
        if (!state.isFull && !hasBeenPulled(in)) popOrPull
      }
    })

    def enqueueForLater(element: In) = {
      buffer.enqueue(element)
    }

  }

  override val shape: FlowShape[In, Out] = FlowShape(in, out)
}

private final class BoundedMap[A, B](maxSize: Int) {

  override def toString = map.toString()

  private val map = new mutable.HashMap[A, B]()

  def +=(kv: (A, B)): this.type = {
    if (map.contains(kv._1)) {
      map += kv
      this
    } else if (!map.contains(kv._1) && map.size >= maxSize) {
      throw new UnsupportedOperationException
    } else {
      map += kv
      this
    }
  }

  def -=(key: A): this.type = {
    map -= key
    this
  }

  def contains(key: A): Boolean = {
    map.contains(key)
  }

  def isFull: Boolean = map.size >= maxSize

  def isEmpty: Boolean = map.isEmpty
}