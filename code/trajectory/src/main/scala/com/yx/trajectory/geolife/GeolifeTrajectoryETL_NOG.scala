package com.yx.trajectory.geolife

import java.time.LocalDate

import com.wang.Utils.SimPathUtils
import com.yx.DBSCANClustering.geolife.{GeoLife_DBSCAN, GeoLife_GDBSCAN}
import com.yx.spark.trajectory.{Bucket, RawTrajectory, TrajectoryData}
import com.yx.trajectory.Utils.CombineByKeyTools._
import com.yx.trajectory.Utils.{CombinerBucketPartTools, OutputComponent, ResultDataPro}
import com.yx.trajectory.{Finished, OneTrajectoriesProcessor, bucketProcessor, mapProcessor}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer


object GeolifeTrajectoryETL_NOG {

  def main(args: Array[String]): Unit = {

    import collection.JavaConversions._

    //Spark应用的基础配置
    val conf = new SparkConf().setAppName("GeolifeTrajectoryETL_NOG")
    if (!conf.contains("spark.master")) {
      conf.setMaster("local[*]")
    }
    //开启kryo序列化机制
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    conf.set("spark.kryo.registrator", "com.yx.trajectory.TrajectoryKryoRegistrator")
    val sc = new SparkContext(conf)

    //数据集
    //    val testTrajectoryRawInputPath = conf.get("spark.TrajectoryETL.testTrajectoryRawInputPath",
    //      "E:\\data\\GeoLife\\Final\\timeuserData.csv")
    //    //E:\data\GeoLife\dayData20090220\geolife20090220.csv
    val testTrajectoryRawInputPath = args(0)

    //输出
    //val OutputPath = conf.get("spark.TrajectoryETL.OutputPath", "data/geolife/")
    val OutputPath = args(1)

    val outputFileType = "text"
    val dataTime = conf.get("spark.GeolifeTrajectoryETL_NOG.dataTime", "20021009")
    //val zk = conf.get("spark.TrajectoryETL.hbase.zk", "master,slave1,slave2")
    //val tableName = conf.get("spark.TrajectoryETL.hbase.tableName", "trajectorySearch2")

    val eps = args(2).toDouble
    val k = args(3).toInt
    val m = args(4).toInt
    //    val eps = 0.0014
    //    val k = 15
    //    val m = 15

    val startTime = System.currentTimeMillis()

    //1.加载数据（原始数据trajectory.txt）并解析、清洗原始数据
    // 0862;1;10/09/2002;09:15:59;23.845089;38.01847;486253.80;4207588.10
    val parsedLogRDD: RDD[TrajectoryData] = sc
      .textFile(testTrajectoryRawInputPath)
      .flatMap(line => RawDataGeolifeParser.parse(line, eps))


    //2.增加优化后的DBSCAN算法
    //10/09/200209:15:59
    //2009-02-2011:46:51
    val DbscanRDD0 = parsedLogRDD
      .map(trajectoryData =>
        (trajectoryData.getDateTime.toString.substring(10, 15), trajectoryData))
      .combineByKey(createCombinerDBSCAN, mergeValueDBSCAN, mergeCombinersDBSCAN)

    val DbscanRDD = DbscanRDD0
      .flatMapValues { DbscanArrayBuffer =>
        val gDBSCAN = new GeoLife_DBSCAN(eps, 10, DbscanArrayBuffer.toArray)
        gDBSCAN.cluster()
        //多线程修改异常：https://blog.csdn.net/weixin_57109001/article/details/122506476
      }

    // (10/09/200209:15:59-Time,{0.8717263713318235,0.5665615867089149,abc-user,clusterID})

    /*val coalesceRDD = DbscanRDD.coalesce(1, false) //测试使用
    val rawTrajectoryOutputPath = "data/output/geolife"
    coalesceRDD.saveAsTextFile(rawTrajectoryOutputPath)*/
    DbscanRDD.count()





    /*//3.形成Key-Value键值对数据格式，并且Key为用户的的手机号
    val phoneGroupedRDD = DbscanRDD
      .map(point => (point._2.getUserID,
        (point._1, point._2.getClusterID)))
    // 字段为：key（用户） value：（时间戳、clusterID）
    // (abc,(09:15:59,1))

    //4.对每一个用户（即每一个phone）的所有信息进行轨迹与基站映射
    val phoneSingleRDD: RDD[(String, RawTrajectory)] = phoneGroupedRDD.mapValues { value =>
      val processor = new OneTrajectoriesProcessor(value._1, value._2) 
      processor.buildRawTrajectory() //生成单个phone的轨迹映射
    }
    // 字段为：key（用户），value：时间戳、balseID（含clusterID信息）
    //(abc,{"timeStamp": 555, "baseId": "555T1"})

    // 5.对每一个用户（即每一个phone）生成映射轨迹
    val mapperTrajectory: RDD[(String, Array[String])] = phoneSingleRDD
      .combineByKey(createCombiner, mergeValue, mergeCombiners) // 4.1 映射轨迹未填充
      .mapValues { arrayTrajectory => //4.2 对映射轨迹进行填充//可优化#######
      val processor = new mapProcessor(arrayTrajectory)
      processor.fillArrayTrajectory()
    }
    //此处的测试代码
    //val mapper =mapperTrajectory.map(line=>(line._1,line._2.mkString(",")))


    //6.生成一个符合分桶数据格式的RDD
    //6.1生成以用户为组的单个用户分桶行程轨迹
    val bucketPhoneRDD: RDD[(String, Bucket)] = mapperTrajectory
      .flatMapValues { iterableMapperTrajectory =>
        val processor = new bucketProcessor(iterableMapperTrajectory, k)
        processor.buildBucket() //可优化#####
      }
      .filter { case (_, value: Bucket) => ResultDataPro.pro(value.getPath.toString) } 


    /*val coalesceRDD = DbscanRDD.coalesce(1, false) //测试使用
    val rawTrajectoryOutputPath = "data/output/trucks"
    coalesceRDD.saveAsTextFile(rawTrajectoryOutputPath)*/


    //6.2 用*扩充轨迹，实现轨迹相似度
    val expandedRDD: RDD[((Int, String), Array[String])] = bucketPhoneRDD
      .map { case (key1: String, value1: Bucket) =>
        ((value1.getBucketId.toInt, key1), value1.getPath.toString)
      }
      .flatMapValues { path =>
        new SimPathUtils().getSimPath(path)
      } //填充*功能
      .map { case (key1: (Int, String), value1: String) =>
      ((key1._1, value1), key1._2)
    } 
      .combineByKey(createCombinerGroup, mergeValueGroup, mergeCombinersGroup) 
      .mapValues(arrayBuffer => arrayBuffer.distinct)
      .map { case (key1: (Int, String), value1: ArrayBuffer[String]) =>
        val pathSplit = key1._2.split("-")
        val pathValue = pathSplit(0)
        var path = pathSplit(1)
        for (i <- 2 until pathSplit.length) {
          path += "-" + pathSplit(i)
        }
        ((key1._1, pathValue), Finished(path, value1))
      }
      .combineByKey(createCombinerGroupSim, mergeValueGroupSim, mergeCombinersGroupSim) 
      .flatMapValues { mapValue =>
      import collection.JavaConversions._
      val treasureMap = new SimPathUtils().mergePathBucket(mapValue.mapValues(line => line.toArray))
      val b = new ArrayBuffer[(String, Array[String])]()
      for ((key, value) <- treasureMap) {
        b += ((key, value))
      }
      b
    } //消除带*路径
      .map { case (key1: (Int, String), value1: (String, Array[String])) =>
      ((key1._1, value1._1), value1._2)
    }


    //7.合并桶
    // 7.1 分users
    val comBucketOneRDD: RDD[((Int, String), String)] = expandedRDD
      .mapValues(arrayBuffer => arrayBuffer.distinct)
      .filter { case (key: (Int, String), value: Array[String]) => value.length >= m } 
      /*.flatMapValues { iterator =>
        val m = iterator.sortWith(_ < _) //注意此处排序的重要性所在
        val processor = new StringTest()
        processor.combination(m)
      }*/
      .map { case (key: (Int, String), value: Array[String]) =>
      (key, value.sortWith(_ < _).mkString("-")) //保持此处排序的重要性所在，并且不再是两两分开
    }


    //7.2 改key，并对相同key的path进行合并
    val comBucketSecondRDD: RDD[(String, String, String, String)] = comBucketOneRDD
      .map { case (key: (Int, String), value: String) => (value, key._1 + "+" + key._2) }
      .combineByKey(createCombinerGroup, mergeValueGroup, mergeCombinersGroup)
      .mapValues { value =>
      val str = value.mkString("|")
      CombinerBucketPartTools.pathTool(str, dataTime)
    } //6.3 字段修改
      .flatMapValues(iterator => iterator.split("\\|"))
      .mapValues { value =>
        val strSplit = value.split(">")
        val pathSplit: String = strSplit(strSplit.length - 1) //取path所在列
      val pathIntArray = pathSplit.split("-")
        var pathStrGeo = pathIntArray(0)
        for (i <- 1 until pathIntArray.length) {
          pathStrGeo += "-" + pathIntArray(i)
        }
        (strSplit(0), strSplit(1), pathStrGeo)
      }
      .filter { case (key: String, value: (String, String, String)) => ResultDataPro.pro(value._3) }
      .map { case (key: String, value: (String, String, String)) =>
        //val phoneArr: Array[String] = key.split("-")
        (key, value._1, value._2, value._3) //定义了四个字段分别为：phoneA、phoneB、startTime和endTime
      }


    //需要行动算子将数据拉回driver
    //comBucketSecondRDD.count()

    val coalesceRDD = comBucketSecondRDD.coalesce(1, false) //测试使用

    // 8.保存结果数据
    // 8.1 HDFS分布式文件系统（text格式+CSV）
    OutputComponent
      .fromOutPutFileType(outputFileType)
      .writeOutputData(sc, OutputPath + "/" + LocalDate.now() + "/" + dataTime, coalesceRDD)*/



    //        TimeUnit.SECONDS.sleep(200)//休眠100秒
    //停止spark应用
    sc.stop()
  }
}



