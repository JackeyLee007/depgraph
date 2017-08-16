package com.zetyun.graphreader.domain

import com.zetyun.graphreader.domain.Query.{LogCall, TransService}

object DTO {
  case class DepgraphResponse(data:Option[List[TransService]], links:Option[List[LogCall]])
}
