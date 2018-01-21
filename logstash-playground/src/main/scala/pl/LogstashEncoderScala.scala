package pl

class LogstashEncoderScala extends net.logstash.logback.encoder.LogstashEncoder {
  override def getCustomFields = s"""{"app":"logback-test-1.0.1","time":"${System.currentTimeMillis()}"}"""
}
