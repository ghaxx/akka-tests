package pl;

public class LogstashEncoderJava extends net.logstash.logback.encoder.LogstashEncoder {
    @Override
    public String getCustomFields() {
        return "{\"app\":\"logback-test-1.0.2\"}";
    }
}
