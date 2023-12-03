package com.yx.trajectory


import com.yx.spark.trajectory.Bucket

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

class bucketProcessor(tjArray: Array[String],k:Int) {

  def buildBucket(): ArrayBuffer[Bucket] = {

    val buckets = new ArrayBuffer[Bucket]()

    //处理的是0-282分桶
    for (count <- 0 until 5) { //count从0到5（不包括5），用于生成5条288
      //滑动单位为5分钟
      //滑动窗口的长度为30

      //从开始节点到结束节点
      for (left <- Range(count, 1440-k+5, 5)) { //left从count开始到1415（不包括，1410），

        breakable({
          if (tjArray(left) == null) {
            break() //continue 1
          }

          var lastSta = "0" //上一个时间节点用户位置
          var curSta = tjArray(left) //此时用户的位置
          val sb = new StringBuilder()
          sb.append(tjArray(left)) //用于存储路径



          breakable({
            for (traIndex <- Range(left + 5, left + k, 5)) {

              val tjIndex = tjArray(traIndex)

              if (tjIndex == null) {
                break() //break
              }

              sb.append("-")
              sb.append(tjIndex)

              val compareCur = curSta

              if (tjIndex != compareCur) {
                lastSta = curSta
                curSta = tjIndex
              }

            }
          })

          /*if (moveTimes == 1) {//此处其实表示的是移动次数为1，而是不能在这30分钟内有点消失
            break()
          }*/

          //添加移动三十分钟的路径到桶中
          val bucket = new Bucket()
          bucket.setBucketId(left / 5)
          val path = sb.toString()
          bucket.setPath(path)
          buckets += bucket

        })
      } //第2个for结束
    }




    /*//处理283-287分桶
    for (count <- 1415 until 1420) {
      //从开始节点到结束节点
      for (left <- Range(count, 1440, 5)) {//此处其实将1435改为1440也可以

        breakable({
          if (tjArray(left) == null) {
            break //continue
          }
          //计算小于三十分钟但是轨迹距离大于五公里的路径
          var distance: Double = 0.0
          var shortKey = ""
          val sb = new StringBuilder()

          var diffBase = 0 //用于标记30分钟内原始采集点个数
          if (tjArray(left).contains("#")) {
            val str = tjArray(left).replaceAll("#", "@")
            sb.append(str) //用于存储路径
          } else {
            diffBase += 1
            sb.append(tjArray(left)) //用于存储路径
          }


          breakable({
            for (traIndex <- Range(left + 5, 1440, 5)) {
              // 0为为填充，-1为经纬度问题，都可以不理会

              var tjIndex = tjArray(traIndex)
              if (tjIndex == null) {
                break() //break
              }

              if (tjIndex.contains("@")) {
                diffBase += 1
              } else {
                tjIndex = tjIndex.replaceAll("#", "@")
              }

              var tjIndexPre = tjArray(traIndex - 5)
              if (tjIndexPre.contains("#")) {
                tjIndexPre = tjIndexPre.replaceAll("#", "@")
              }


              if (!tjIndex.contains("@") || !tjIndexPre.contains("@")) {
                break //break
              }

              sb.append("-")
              sb.append(tjIndex)
              distance += geoHashMapUtils
                .getDistance(tjIndex, tjIndexPre)
              if (distance > 5000.0) {
                shortKey = sb.toString
                break //break
              }
            }
          })

          if (diffBase <= 2) {
            break() //continue
          }

          if (shortKey.contains("E")){
            break()
          }

          if (shortKey != "") {
            val bucket = new Bucket()
            bucket.setBucketId(left / 5)
            val path = baseIdPath(shortKey, labelMap)
            bucket.setPath(path)
            buckets += bucket
          }
        })
      }
    }*/

    buckets
  }

}
