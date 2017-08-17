package com.zetyun.graphreader.controller
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import com.zetyun.graphreader.domain.Query.{DepGraph, QueryParam}
import com.zetyun.graphreader.domain.RestJsonProtocol._
import com.zetyun.graphreader.service.LogCallService
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
object BaseRouter {



  private[this] final val logger = LoggerFactory.getLogger(this.getClass)

  val respondWithDomainHeader =
    respondWithHeaders(RawHeader("Access-Control-Allow-Origin", "*"),
      RawHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS"),
      RawHeader("Access-Control-Allow-Headers", "Referer,Accept,Origin,Content-Type,Authorization,User-Agent,If-Modified-Since"))


  val router = path("depgraph") {
    get {
      parameters('date, 'transcode) { (date, transcode) =>  {
        respondWithDomainHeader {
          val ok: Future[DepGraph] = Future(LogCallService.getLogCalls(transcode, date))
          onComplete(ok) { done =>
            logger.info("query depgraph by transcode {} and date ", transcode)

            complete(done)
          }
        }
      }}
    }
  }
}
