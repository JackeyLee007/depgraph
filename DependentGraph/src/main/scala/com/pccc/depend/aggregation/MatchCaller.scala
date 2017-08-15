package com.pccc.depend.aggregation

import org.apache.spark.sql.expressions.Aggregator

import scala.collection.mutable
import org.apache.spark.sql.Encoder
import org.apache.spark.sql.Encoders


case class Log(codeinfo:String, transcode:String, host:String, ip:String, url:String, servicename:String, source_type:String, transport:String, time:String, globalseqno:String)
case class Caller(var code:String)

object MatchCaller extends Aggregator[Log, Caller, String] {
  def zero : Caller = Caller("")

  def reduce(buffer:Caller, log:Log) : Caller = {
    if(log.source_type == "pmstoudasystem"){
      buffer.code = log.transcode
    }
    buffer
  }

  def merge(buffer1:Caller, buffer2:Caller): Caller = {
    if(buffer1.code.isEmpty)
      buffer2
    else
      buffer1
  }

  def finish(buffer:Caller):String = {
    buffer.code
  }

  // Specifies the Encoder for the intermediate value type
  def bufferEncoder: Encoder[Caller] = Encoders.product
  // Specifies the Encoder for the final output value type
  def outputEncoder: Encoder[String] = Encoders.STRING
}
