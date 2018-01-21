package pl

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.Converter

class EnvConverter extends ClassicConverter {

  def convert(event: ILoggingEvent): String = {
    "My Env"
  }
}