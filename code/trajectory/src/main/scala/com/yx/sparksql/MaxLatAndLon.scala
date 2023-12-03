package com.yx.sparksql

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType}

object MaxLatAndLon {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("LatAndLon")
      .getOrCreate()

    //1.从文件中获取dataFrame
    val df:DataFrame = spark.read
      .schema(StructType(List(
        StructField("user",StringType,true),//用户
        StructField("type",StringType,true),//类型
        StructField("time1",StringType,true),//时间1
        StructField("time2",StringType,true),//时间2
        StructField("lat",DoubleType,true),//纬度
        StructField("lon",DoubleType,true),//经度
        StructField("str1",StringType,true),//str1
        StructField("str2",StringType,true)//str2
      )))
      .option("sep",";")
      .csv("E:\\大数据时空数据分布式伴随研究\\数据集\\trucks\\trucks-新\\Trucks1\\trucks0910.csv")

    df.createOrReplaceTempView("test")

    val dfSave = spark.sql("select max(lon) max_lon,min(lon) min_lon,max(lat) max_lat,min(lat) min_lat from test ").repartition(1)

    dfSave.show()

   /*dfSave.write.mode(SaveMode.Overwrite).option("sep",",")
      .csv("E:\\大数据时空数据分布式伴随研究\\数据集\\T-drive\\TDrive8")*/


    spark.stop()
  }
}
