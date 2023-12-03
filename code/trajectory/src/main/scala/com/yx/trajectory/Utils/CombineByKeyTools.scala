package com.yx.trajectory.Utils


import com.yx.DBSCANClustering.Point
import com.yx.spark.trajectory.{RawTrajectory, TrajectoryData}
import com.yx.trajectory.Finished

import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

object CombineByKeyTools {

  def createCombinerDBSCAN:TrajectoryData=>ArrayBuffer[Point] = (value:TrajectoryData)=>{
    val point = new Point(value.getPhone.toString,
      value.getLon.toString.toDouble,value.getLat.toString.toDouble,
      value.getConnectionType.toString.toLong)
    ArrayBuffer(point)
  }

  def mergeValueDBSCAN:(ArrayBuffer[Point],TrajectoryData) => ArrayBuffer[Point] =
    (acc:ArrayBuffer[Point],value:TrajectoryData)=>{
      val point = new Point(value.getPhone.toString,
        value.getLon.toString.toDouble,value.getLat.toString.toDouble,
        value.getConnectionType.toString.toLong)
      acc += point
    }

  def mergeCombinersDBSCAN:(ArrayBuffer[Point], ArrayBuffer[Point]) => ArrayBuffer[Point] =
    (buf1: ArrayBuffer[Point], buf2: ArrayBuffer[Point]) => buf1 ++= buf2



  def createCombiner: RawTrajectory => Array[String] = (value: RawTrajectory) => {
    val trArrayStart = new Array[String](1440)
    trArrayStart(value.getTimeStamp) = value.getBaseId.toString
    trArrayStart
  }

  def mergeValue: (Array[String], RawTrajectory) => Array[String] = (acc: Array[String], value: RawTrajectory) => {
    //val trArray = new Array[String](1440)
    //acc.copyToArray(trArray)
    //trArray(value.getTimeStamp) = value.getBaseId.toString
    acc(value.getTimeStamp) = value.getBaseId.toString
    acc
  }

  def mergeCombiners: (Array[String], Array[String]) => Array[String] = (acc1: Array[String], acc2: Array[String]) => {
    //val trArrayEnd = new Array[String](1440)
    val trArrayEnd = acc1
    //+++++++++++++++
    for (i <- 0 until acc2.length) {
      if (acc2(i) != null) {
        trArrayEnd(i) = acc2(i)
      }
    }
    trArrayEnd
  }


  def createCombinerGroup: String => ArrayBuffer[String] =
    (value: String) => ArrayBuffer(value)

  def mergeValueGroup: (ArrayBuffer[String], String) => ArrayBuffer[String] =
    (buf: ArrayBuffer[String], value: String) => buf += value

  def mergeCombinersGroup: (ArrayBuffer[String], ArrayBuffer[String]) => ArrayBuffer[String] =
    (buf1: ArrayBuffer[String], buf2: ArrayBuffer[String]) => buf1 ++= buf2





  def createCombinerGroupSim:Finished => Map[String,ArrayBuffer[String]] = {
    value:Finished  =>
      val treasureMap = Map[String,ArrayBuffer[String]]()
      treasureMap += (value.path -> value.phone)
  }
  def mergeValueGroupSim:(Map[String,ArrayBuffer[String]],Finished) => Map[String,ArrayBuffer[String]]  =
    (acc:Map[String,ArrayBuffer[String]],value:Finished) => {
      acc += (value.path -> value.phone)
    }

  def mergeCombinersGroupSim:(Map[String,ArrayBuffer[String]],Map[String,ArrayBuffer[String]])=>Map[String,ArrayBuffer[String]] =
    (acc1:Map[String,ArrayBuffer[String]],acc2:Map[String,ArrayBuffer[String]]) =>{
      acc1 ++= acc2
    }








  def createCombinerBucket: Int => ArrayBuffer[Int] =
    (value: Int) => ArrayBuffer(value)

  def mergeValueBucket: (ArrayBuffer[Int], Int) => ArrayBuffer[Int] =
    (buf: ArrayBuffer[Int], value: Int) => buf += value

  def mergeCombinersBucket: (ArrayBuffer[Int], ArrayBuffer[Int]) => ArrayBuffer[Int] =
    (buf1: ArrayBuffer[Int], buf2: ArrayBuffer[Int]) => buf1 ++= buf2

}
