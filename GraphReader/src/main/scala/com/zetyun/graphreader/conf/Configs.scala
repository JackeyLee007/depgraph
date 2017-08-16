package com.zetyun.graphreader.conf

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}

/**
  * Created by lumk on 17-6-14.
  */
object Configs {

  var conf: Config = ConfigFactory.parseFile(new File("application.conf"))

  if (conf.isEmpty)
    conf = ConfigFactory.load()

  // server settings
  val serverHost: String = conf.getString("server.host")
  val serverPort: Int = conf.getInt("server.port")

  val hbaseAddr = conf.getString("hbase.zk")
}
