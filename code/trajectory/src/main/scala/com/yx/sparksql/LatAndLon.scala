package com.yx.sparksql

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType}

object LatAndLon {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("LatAndLon")
      .getOrCreate()

    //1.从文件中获取dataFrame
    val df:DataFrame = spark.read
        .schema(StructType(List(
          StructField("oid",StringType,true),
          StructField("date",StringType,true),
          StructField("lat",DoubleType,true),//纬度
          StructField("lon",DoubleType,true)//经度
        )))
        .option("sep",",")
        .csv("E:\\大数据时空数据分布式伴随研究\\数据集\\T-drive\\T-drive Taxi Trajectories\\release\\taxi_log_2008_by_id")



    df.createOrReplaceTempView("test")

    val dfSave = spark.sql("select * from test where substring(date,1,10)=='2008-02-08'").repartition(1)




    /*dfSave.write.mode(SaveMode.Overwrite).option("sep",";")
      .csv("E:\\大数据时空数据分布式伴随研究\\数据集\\T-drive\\TDrive1")*/


    dfSave.write.mode(SaveMode.Overwrite).option("sep",",")
      .csv("E:\\大数据时空数据分布式伴随研究\\数据集\\T-drive\\TDrive8")


    spark.stop()
  }
}
