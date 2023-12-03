package com.yx.trajectory.Utils

import java.io.StringWriter
import java.time.LocalDate

import au.com.bytecode.opencsv.CSVWriter
import org.apache.hadoop.fs.Path
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
  * 输出组件
  */

trait OutputComponent {

  /**
    * 保存结果数据的方法
    */
  def writeOutputData(sc: SparkContext, baseOutputPath:String,
                      comBucketSecondRDD:RDD[(String,String,String,String)])={
    deleteIfExists(sc, baseOutputPath)
  }

  //这个方法是子类中公共方法，进行提取写进父类trait中
  private def deleteIfExists(sc: _root_.org.apache.spark.SparkContext, OutputPath: _root_.scala.Predef.String) = {
    //尤其注意下面的FileSystem和Path都是apache hadoop中的类型，不是java中的类型
    val path = new Path(OutputPath)
    val fileSystem = path.getFileSystem(sc.hadoopConfiguration)
    if (fileSystem.exists(path)) {
      fileSystem.delete(path, true) //如果已创建就删除
    }
  }

}


//用于接口对外提供服务（写工厂方法作为静态类，放在伴生对象中）
object OutputComponent{
  def fromOutPutFileType(fileType:String)={
    if (fileType.equals("text")){
      new TextFileOutput
    }else{
      new TextFileOutput//后期根据指定格式修改
    }
  }
}


class TextFileOutput extends OutputComponent{
  /**
    * 保存结果数据的方法
    */
  override def writeOutputData(sc: SparkContext, baseOutputPath: String,
                               comBucketSecondRDD:RDD[(String,String,String,String)]): Unit = {
    super.writeOutputData(sc,baseOutputPath,comBucketSecondRDD)

    //保存结果数据(以text格式保存数据)
    val trackerLogOutputPath = s"${baseOutputPath}/" //后期可以改为HDFS文件目录路径
    //deleteIfExists(sc, trackerLogOutputPath)
    comBucketSecondRDD
      .map { line =>
        List(line._1,line._2,line._3,line._4).toArray
      }
      .mapPartitions(bucket => {
      import scala.collection.JavaConversions._
      val stringWriter = new StringWriter()
      val csvWriter = new CSVWriter(stringWriter,',',CSVWriter.NO_QUOTE_CHARACTER)
      csvWriter.writeAll(bucket.toList)
      Iterator(stringWriter.toString)
    }).saveAsTextFile(trackerLogOutputPath)

  }

}