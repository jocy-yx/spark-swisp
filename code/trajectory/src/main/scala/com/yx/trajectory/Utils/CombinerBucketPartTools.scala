package com.yx.trajectory.Utils

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object CombinerBucketPartTools {

  def pathTool(str1:String,dataTime:String):String ={
    val arr = str1.split("\\|")
    val map = mutable.Map[String,Int]()
    val str2 = new StringBuilder()

    for (i <- 0 until arr.length){
      val maparr = arr(i).split("\\+")
      map += (maparr(1) -> maparr(0).toInt)
    }

    val mapSort = map.toSeq.sortBy(_._2)

    for (i<-mapSort){
        str2.append(i._2+"+"+i._1+"|")
    }
    //println(str2.toString)//测试使用
    ToolPathTime(str2.toString,dataTime)
  }

  def ToolPathTime(strSort:String,dataTime:String):String={
    /*val strSort = "95+20677-20677-20677-94951-94951-20677|96+20677-20677-94951-94951-20677-20677|97+20677-94951-94951-20677-20677-20677|186+20677-94951-94951-94951-94951-20677|"
    val strSort2 = "95+20677-20677-20677-94951-94951-20677|"*/
    val arr = pathLong(strSort).split("\\|")
    val mapKV = mutable.Map[Int, String]() //用于存放递增分桶id 与 对应的基站id
    val arrID = new ArrayBuffer[Int]() //用于存放有序的分桶id
    val strResult = new mutable.StringBuilder()
    val strFinalResult = new mutable.StringBuilder()

    val arrIDMapKV = mutable.Map[Int,Int]()//新增

    for (i <- 0 until arr.length) { //首先最外层按照 | 划分
      val splitWithJ = arr(i).split("\\+")
      val valueInt = splitWithJ(0).toInt
      arrID += valueInt
      var start = splitWithJ(0).toInt + 1
      val arrBaseId = splitWithJ(1).split("-")

      arrIDMapKV += ( valueInt -> (valueInt+arrBaseId.length))//新增

      for (j <- 0 until arrBaseId.length) { //其次按照 - 划分，能获得mapKV中的V
        mapKV += (start -> arrBaseId(j))
        start += 1
      }
    }

    var flag1 = arrID(0)
    val end = arrID(arrID.length - 1)

    import scala.util.control.Breaks._
    breakable {
      for (i <- arrID.indices) {
        if (arrID(i) == end) {
          strResult.append(arrID(i) + ";")
          break()
        }

        if (arrID(i + 1) == flag1 + 1) {
          strResult.append(arrID(i) + ",")
          flag1 += 1
        } else if( arrIDMapKV.getOrElse(arrID(i),arrID(i)) >= arrID(i+1)  ){ //新增
          flag1 = arrID(i + 1)
          strResult.append(arrID(i) + ",")
        }else {
          flag1 = arrID(i + 1)
          strResult.append(arrID(i) + ";")
        }
      }
    }


    //val mapSort = mapKV.toSeq.sortBy(_._1).toMap//?对map进行了排序，需要对arrID中数据排序吗

    val array = strResult.toString().split(";")

    for (j<-0 until array.length){
      if (!array(j).contains(",")){
        val startStep = array(j).toInt + 1 //96
        var endStep = startStep
        while(mapKV.contains(endStep)){
          endStep += 1
        }
        endStep -= 1
        strFinalResult.append(dataTime
          +String.format("%02d",Integer.valueOf( (startStep-1)*5/60 ) )
          +String.format("%02d",Integer.valueOf((startStep-1)*5%60))+">"
          +dataTime
          +String.format("%02d",Integer.valueOf( endStep*5/60 ) )
          +String.format("%02d",Integer.valueOf(endStep*5%60))+">")
        for (k <- startStep to endStep ){
          strFinalResult.append(mapKV.getOrElse(k,-1)+"-")
        }
        strFinalResult.append("|")
      }else{
        val splitArray = array(j).split(",")
        val start = splitArray(0).toInt + 1 //197
        var end2 = start
        while(mapKV.contains(end2)){
          end2 += 1
        }
        end2 -= 1
        strFinalResult.append(dataTime
          +String.format("%02d",Integer.valueOf( (start-1)*5/60 ) )
          +String.format("%02d",Integer.valueOf((start-1)*5%60))+">"
          +dataTime
          +String.format("%02d",Integer.valueOf( end2*5/60 ) )
          +String.format("%02d",Integer.valueOf(end2*5%60))+">")
        for (k <- start to end2 ){
          strFinalResult.append(mapKV.getOrElse(k,-1)+"-")
        }
        strFinalResult.append("|")
      }
    }
    strFinalResult.mkString
  }


  def pathLong(strSort: String):String ={ //只负责baseId相同，但是有短长共存以及路径不同的情况
    val longPathMap = mutable.Map[Int, String]()
    val arr = strSort.split("\\|")

    if (arr.length == 1) {
      val arrSingle = arr(0).split("\\+")
      longPathMap += (arrSingle(0).toInt -> arrSingle(1))
    } else {
      val arrSingleFirst = arr(0).split("\\+")
      longPathMap += (arrSingleFirst(0).toInt -> arrSingleFirst(1))
      for (i <- 1 until arr.length) {
        val arrSingle = arr(i).split("\\+")
        if ((arrSingle(0) == arr(i - 1).split("\\+")(0)
          && arrSingle(1).length >= arr(i - 1).split("\\+")(1).length)
          || arrSingle(0) != arr(i - 1).split("\\+")(0)) {
          longPathMap += (arrSingle(0).toInt -> arrSingle(1))
        }
      }
    }

    val strResult = new mutable.StringBuilder()
    val mapSort = longPathMap.toSeq.sortBy(_._1)
    for ((key, value) <- mapSort) {
      strResult.append(key + "+" + value + "|")
    }

    strResult.toString()
  }


}
