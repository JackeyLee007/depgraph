package com.pccc.depend.transform

import org.apache.spark.sql.functions.udf

object DepTrans {
  val mk_Caller:((String, String)=>String) = (transcode:String, source_type:String)=>{
    var res:String = ""
    if(source_type == "pmstoudasystem"){
      res = transcode
    }else{
      res = ""
    }

    res

  }
  val mk_CallerTime:((String, String)=>String) = (time:String, source_type:String)=>{
    var res:String = ""
    if(source_type == "pmstoudasystem"){
      res = time
    }else{
      res = ""
    }

    res
  }
  val mk_Callee:((String, String)=>String) = (transcode:String, source_type:String)=>{
    var res:String = ""
    if(source_type == "pmstoudaproxy"){
      res = transcode
    }else{
      res = ""
    }

    res

  }
  val mk_CalleeTime:((String, String)=>String) = (time:String, source_type:String)=>{
    var res:String = ""
    if(source_type == "pmstoudaproxy"){
      res = time
    }else{
      res = ""
    }

    res
  }

  val mkCaller = udf(mk_Caller)
  val mkCallerTime = udf(mk_CallerTime)
  val mkCallee = udf(mk_Callee)
  val mkCalleeTime = udf(mk_CalleeTime)

}
