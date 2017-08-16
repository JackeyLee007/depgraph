package com.zetyun.graphreader.service

import com.zetyun.graphreader.domain.Query.DepGraph
import com.zetyun.graphreader.repository.LogCallRepo

object LogCallService {

//  LogCallRepo.open()

  def getLogCalls(transCode:String, date:String) : DepGraph = {
      LogCallRepo.getDepGraph(transCode, date)
  }
}
