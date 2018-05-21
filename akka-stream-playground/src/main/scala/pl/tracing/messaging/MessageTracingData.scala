package pl.tracing.messaging

import java.time.{ZoneOffset, ZonedDateTime}

case class MessageTracingData (
  correlationId: CorrelationId,
  messageId: MessageId,
  previousMessageId: Option[MessageId],
  generation: Int,
  creationTime: ZonedDateTime,
  description: String,
  source: Service,
  target: Service
)

private object MessageTracingData {
  def createNew(description:String, source: Service, target: Service) =
    MessageTracingData(
      CorrelationId.createNew,
      MessageId.createNew,
      None,
      0,
      ZonedDateTime.now(ZoneOffset.UTC),
      description,
      source,
      target
    )

  def createFollowUp(tracingData: MessageTracingData, description: String, source: Service, target: Service) =
    MessageTracingData(
      tracingData.correlationId,
      MessageId.createNew,
      Some(tracingData.messageId),
      tracingData.generation + 1,
      ZonedDateTime.now(ZoneOffset.UTC),
      description,
      source,
      target
    )
}