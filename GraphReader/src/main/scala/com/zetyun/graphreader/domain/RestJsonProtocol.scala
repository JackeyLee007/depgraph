package com.zetyun.graphreader.domain

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.zetyun.graphreader.domain.Query._
import spray.json.DefaultJsonProtocol

object RestJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol{

  implicit val NodeJson = jsonFormat1(TransService)
  implicit val LogCallJson = jsonFormat2(LogCall)
  implicit val DepgraphJson = jsonFormat2(DepGraph)
  implicit val queryParam = jsonFormat2(QueryParam)
}
