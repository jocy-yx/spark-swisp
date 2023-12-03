package com.yx.sparksql

import java.util

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object FlatMapTest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("FlatMapTest")
      .getOrCreate()

    import spark.implicits._


    val RDD:RDD[(Int,Seq[Int])] = spark
      .sparkContext
      .parallelize(Seq((1,Set(1,2,3).toSeq),(2,Set(4,5,6).toSeq)))

    val dataFrame = spark.createDataFrame(RDD)
        .withColumnRenamed("_1","oid")
        .withColumnRenamed("_2","cid")


    dataFrame.show()


    dataFrame.createOrReplaceTempView("test")


    spark.udf.register("myudf",(arg1:Int,arg2:Seq[Int])=>{
      val array = new Array[String](arg2.length)
      for (i <- 0 until arg2.length){
        array(i) = "2021MMddhhmm,"+arg1+","+arg2(i)
      }
      array.mkString("_")
    })

    val df = dataFrame.selectExpr("myudf(oid,cid) as c")
      .flatMap(fun => {
        val strings = fun.getString(0).split("_")
        strings })


    df.selectExpr()


    spark.stop()
  }
}
