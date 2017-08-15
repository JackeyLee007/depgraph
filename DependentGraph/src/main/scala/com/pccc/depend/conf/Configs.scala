package com.pccc.depend.conf

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}

object Configs {
  var conf: Config = ConfigFactory.parseFile(new File("application.conf"))

  if(conf.isEmpty)
    conf = ConfigFactory.load()

  val kafkaBrokerList: String = conf.getString("kafka.brokerList")
  val kafkaTopic: String = conf.getString("kafka.topic")
  val kafkaAck: String = conf.getString("kafka.ack")
  val kafkaRetries: String = conf.getString("kafka.retries")
  val kafkaBatchSize: Integer = conf.getInt("kafka.batchSize")
  val kafkaConsumerGroup: String = conf.getString("kafka.consumerGroup")

}
