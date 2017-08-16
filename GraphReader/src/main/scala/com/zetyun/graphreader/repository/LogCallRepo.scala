package com.zetyun.graphreader.repository

import com.zetyun.graphreader.conf.Configs
import com.zetyun.graphreader.domain.Query.{DepGraph, LogCall, TransService}
import org.apache.hadoop.hbase.{CellScanner, HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.filter.{CompareFilter, RowFilter, SubstringComparator}

import scala.collection.mutable


object LogCallRepo {
  val hbaseAddr = Configs.hbaseAddr
  var conn: Connection = _
  var table: Table = _

  def open()  = {
    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.quorum", hbaseAddr)
    conn = ConnectionFactory.createConnection(conf)
    table = conn.getTable(TableName.valueOf("calls"))
  }

  def close() = {
    conn.close()
  }

  open()
  def getDepGraph(transCode:String, date:String): DepGraph = {

    val nodes = mutable.MutableList[TransService]()
    val logcalls = mutable.MutableList[LogCall]()

    val scan = new Scan()
//    val filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator(date+"|"+transCode))
//    scan.setFilter(filter)

    scan.addColumn("caller".getBytes(), "code".getBytes())
    scan.addColumn("callee".getBytes(), "code".getBytes())
    val scanner: ResultScanner  = table.getScanner(scan)
    val res = scanner.iterator()
    while(res.hasNext){
      val data = res.next()
      val caller = data.getValue("caller".getBytes(), "code".getBytes())
      val callee = data.getValue("callee".getBytes(), "code".getBytes())

      nodes += TransService(caller.toString)
      nodes += TransService(callee.toString)

      val link = LogCall(caller.toString, callee.toString)
      logcalls += link
    }

    DepGraph(Option(nodes.toList), Option(logcalls.toList))

  }
}
