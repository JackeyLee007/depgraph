package com.zetyun.graphreader.domain

import java.sql.Timestamp

object Query {
  case class TransService(name:String)
  case class LogCall(source:String, target:String)
  case class DepGraph(data: Option[List[TransService]], links: Option[List[LogCall]])

  case class QueryParam(transcode:String, date:String)

}
