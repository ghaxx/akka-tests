package pl.tracing.messaging


case class Message private(
  command: Command,
  tracingData: MessageTracingData
)

object Message {
  def createNew(command: Command, source: Service, target: Service) =
    new Message(command, MessageTracingData.createNew(command.name, source, target))

  def createFollowUp(command: Command, previousTracingData: MessageTracingData, source: Service, target: Service) =
    new Message(command, MessageTracingData.createFollowUp(previousTracingData, command.name, source, target))
}