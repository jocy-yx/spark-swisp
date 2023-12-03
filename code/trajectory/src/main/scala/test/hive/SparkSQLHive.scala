package test.hive

import java.io.File

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession}

object SparkSQLHive {
  def main(args: Array[String]): Unit = {
    // 0.配置
    val warehouseLocation = new File("spark-warehouse").getAbsolutePath//此处是放在当前项目目录中

    val spark = SparkSession
      .builder()
      .appName("SparkSQLHive")
      .config("spark.sql.warehouse.dir", warehouseLocation)
      .master("local")//本地测试代码，可对比在集群运行此处的区别
      .enableHiveSupport()//支持Hive
      .getOrCreate()

    import spark.implicits._
    import spark.sql


    // 1.导入数据（本地模式，后期要调整为集群模式）
    sql("CREATE DATABASE IF NOT EXISTS trajectory")//SparkSession的sql API，创建twq数据库

    sql(
      """
        |CREATE TABLE IF NOT EXISTS trajectory.RawBaseIdFile (
        | geoHash string,
        | latAndLon string,
        | baseId int)
        |ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
      """.stripMargin)//创建表

    sql("LOAD DATA LOCAL INPATH 'src/main/resources/000000_0' OVERWRITE INTO TABLE trajectory.RawBaseIdFile")

    //val firstDf = sql("select * from trajectory.RawBaseIdFile")
    //firstDf.show()


    // 2.增加集合字段[经纬度1-baseId1, 经纬度2-baseId2, ……]
    val broastRDD =
      sql("select  collect_set(concat_ws('-',latAndLon,baseId)) AS newInfo  from trajectory.RawBaseIdFile")
        .rdd

    val rdd_String:RDD[String] = broastRDD.map(_.mkString(","))


    val rddb = rdd_String.coalesce(1)
    val mapTrajectoryOutputPath = "data/output/mapTrajectory"
    rddb.saveAsTextFile(mapTrajectoryOutputPath)







    /*secondDf.coalesce(1)
      .write
      .format("json")
      .mode(SaveMode.Overwrite)
      .save("output/result.json")*/

    spark.stop()
  }
}


case class RawBaseIdFile(geoHash:String,latAndLon:String,baseId:Int)