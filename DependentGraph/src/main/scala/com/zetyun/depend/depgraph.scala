package com.zetyun.depend

import java.io.{File, PrintWriter}
import java.sql.Timestamp

import com.zetyun.depend.conf.Configs
import com.zetyun.depend.outsink.HBaseWriter
import com.zetyun.depend.transform.DepTrans
import org.apache.log4j.PropertyConfigurator
import org.apache.spark.sql.{ForeachWriter, Row, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.types._



case class LogCall(globalseqno:String, servicename:String, time:Timestamp, caller:String, callertime:String, callee:String, calleetime:String)


object depgraph {

  //                  0         1         2   3   4  5           6           7         8    9
  val schemaString = "codeinfo,transcode,host,ip,url,servicename,source_type,transport,time,globalseqno"

  // Generate the schema based on the string of schema
  val fields = schemaString.split(",")

  val field_i:((String, String, Int)=>String)= (value:String, sep:String, idx:Int)=>{
    value.split(sep).apply(idx)
  }

//  val schema = StructType(fields.map(fieldName => StructField(fieldName, StringType, nullable = true)))

  val genCol = udf(field_i)


  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .appName("DependencyGraph")
      .master("local[2]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    import spark.implicits._


    // Subscribe to 1 topic
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", Configs.kafkaBrokerList)
      .option("subscribe", Configs.kafkaTopic)
      .load()


    var logs = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")

    // Split the value with "," and add each field as a new column
    for(i <- 0 to fields.length-1){
      logs = logs.withColumn(fields(i), genCol(col("value"), lit(","), lit(i)))
    }

    logs = logs
      .withColumn("caller", DepTrans.mkCaller(col("transcode"), col("source_type")))
      .withColumn("callertime", DepTrans.mkCallerTime(col("time"), col("source_type")))
      .withColumn("callee", DepTrans.mkCallee(col("transcode"), col("source_type")))
      .withColumn("calleetime", DepTrans.mkCalleeTime(col("time"), col("source_type")))


    logs.printSchema()

    //  Match the caller and callee
    logs.createOrReplaceTempView("calls")
    val query = logs.select("globalseqno","servicename", "time", "caller", "callertime", "callee", "calleetime").as[LogCall]
      .groupBy(
        window($"time", "5 seconds", "1 seconds"),
        $"globalseqno"
        ).agg(max("servicename").as("servicename"), max("caller").as("caller"), max("callertime").as("callertime"), max("callee").as("callee"), max("calleetime").as("calleetime"))
      .select(col("globalseqno"), col("servicename"), col("callertime").as("time"), col("caller"), col("callertime"), col("callee"), col("calleetime"))
      .as[LogCall]

//    val result = query.writeStream.outputMode("update").format("console").start()
//    result.awaitTermination()

    val result = query.writeStream
//      .option("checkpointLocation", "./output")
      .outputMode("update")
      .foreach(new HBaseWriter).start()
    result.awaitTermination()
  }

}
