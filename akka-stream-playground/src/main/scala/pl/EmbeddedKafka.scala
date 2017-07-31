package pl

import java.util.Properties

import kafka.server.{KafkaConfig, KafkaServer, KafkaServerStartable}

class EmbeddedKafka {

  def start() = {
    val kafkaProperties: Properties = null
    val zkProperties: Properties = null
    val kafkaConfig = new KafkaConfig(kafkaProperties)

    //start local zookeeper
    System.out.println("starting local zookeeper...")
    val zookeeper = new ZooKeeperLocal(zkProperties)
    System.out.println("done")

    //start local kafka broker
    val kafka = new KafkaServerStartable(kafkaConfig)
    System.out.println("starting local kafka broker...")
    kafka.startup
  }
}
