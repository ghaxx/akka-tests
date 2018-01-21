package pl.tracing.messaging

import java.time.{ZoneOffset, ZonedDateTime}

private case class MessageTracingData private (
  correlationId: CorrelationId,
  trace: List[MessageId],
  description: String,
  creationDate: ZonedDateTime,
  source: Service,
  target: Service
)

private object MessageTracingData {
  def createNew(description:String, source: Service, target: Service) =
    new MessageTracingData(
      CorrelationId.createNew,
      List(MessageId.createNew),
      description,
      ZonedDateTime.now(ZoneOffset.UTC),
      source,
      target
    )

  def createFollowUp(tracingData: MessageTracingData, description: String, source: Service, target: Service) =
    new MessageTracingData(
      tracingData.correlationId,
      tracingData.trace :: MessageId.createNew,
      description,
      ZonedDateTime.now(ZoneOffset.UTC),
      source,
      target
    )
}