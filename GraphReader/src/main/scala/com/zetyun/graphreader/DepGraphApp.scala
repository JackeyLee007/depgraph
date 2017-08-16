package com.zetyun.graphreader

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.zetyun.graphreader.conf.Configs
import com.zetyun.graphreader.conf.Configs
import com.zetyun.graphreader.controller.BaseRouter
import org.slf4j.LoggerFactory

object DepGraphApp {

  private[this] final val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    val router = BaseRouter.router
    Http().bindAndHandle(router, Configs.serverHost, Configs.serverPort)
    logger.info("server ready ...... ")

  }

}
