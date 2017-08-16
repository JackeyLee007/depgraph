package com.zetyun.depend.outsink

import java.io.{File, PrintWriter}
import java.util.{Calendar, UUID}

import com.zetyun.depend.LogCall
import com.zetyun.depend.conf.Configs
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory, Put, Table}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.ForeachWriter
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.ArrayBuffer

class HBaseWriter extends ForeachWriter[LogCall]{
  val logger = LoggerFactory.getLogger("HBaseWriter")
  val ZOOKEEPER_QUORUM = Configs.hbaseAddr


  var conn:Connection =_
  var table:Table = _

  def open(partitionId: Long, version: Long): Boolean = {
    // open connection
    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.quorum", ZOOKEEPER_QUORUM)
    conn = ConnectionFactory.createConnection(conf)
    table = conn.getTable(TableName.valueOf("calls"))
    true
  }

  def process(log: LogCall) = {
    // write string to connection

    if (!log.globalseqno.isEmpty && !log.servicename.isEmpty && !log.caller.isEmpty && !log.callee.isEmpty) {
      try {
        val seqno = UUID.fromString(log.globalseqno).hashCode()
        val servicename = log.servicename.hashCode()

        val time = log.time.toString.substring(0, 10)
        val rowkey = "%d|%d|%s|%s".format(seqno, servicename, time, log.caller)
        val put = new Put(rowkey.getBytes)

        put.addColumn(Bytes.toBytes("caller"), Bytes.toBytes("code"), Bytes.toBytes(log.caller))
        put.addColumn(Bytes.toBytes("caller"), Bytes.toBytes("time"), Bytes.toBytes(log.callertime))

        put.addColumn(Bytes.toBytes("callee"), Bytes.toBytes("code"), Bytes.toBytes(log.callee))
        put.addColumn(Bytes.toBytes("callee"), Bytes.toBytes("time"), Bytes.toBytes(log.calleetime))

        table.put(put)
      }
      catch {
        case ex: IllegalArgumentException => logger.debug("Illegal globalseqno" + log.globalseqno)
      }
    }
  }

  def close(errorOrNull: Throwable): Unit = {
    // close the connection
    conn.close()
  }
}
