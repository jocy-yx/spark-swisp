package com.yx.trajectory

class mapProcessor(trArrayEnd: Array[String]) {

  def fillArrayTrajectory(): Array[String] = { //填充最原始的1440数组
    val trLength = 1440 //数组长度为1440

    var rawTrajectoryIndex = 0 //快指针
    var left = 0 //left是用于填充的指针
    while (rawTrajectoryIndex < trLength && trArrayEnd(rawTrajectoryIndex) == null) {
      rawTrajectoryIndex += 1
    } //此循环使得rawTrajectoryIndex指向第一个不为0（即初始已填充的数组索引处）

    //将第一个非零数字下标的左边都填充上该下标的值，如果间隔大于15就不填
    if (rawTrajectoryIndex - left >= 15) {
      left = rawTrajectoryIndex
    }
    while (left < rawTrajectoryIndex) {
      trArrayEnd(left) = trArrayEnd(rawTrajectoryIndex)
      left += 1
    } //此循环填充了一开始全是0的情况，都赋值为第一个不为0的基站ID


    import scala.util.control.Breaks._
    breakable {//此处的break验证有效
      while (rawTrajectoryIndex < trLength) {
        //接下去的处理都是在初始数组时第一个已填充的位置到288数组结束的填充方法
        while (rawTrajectoryIndex < trLength && trArrayEnd(rawTrajectoryIndex) != null) {
          left += 1
          rawTrajectoryIndex += 1
        } //当遇到初始已填充点，left和rawTrajectoryIndex指针都向后移动一位
        while (rawTrajectoryIndex < trLength && trArrayEnd(rawTrajectoryIndex) == null) {
          rawTrajectoryIndex += 1
        } //当遇到初始未填充点，rawTrajectoryIndex向后移动一位

        if (rawTrajectoryIndex == trLength) {
          if (rawTrajectoryIndex -left >= 15){
            break
          }
          for (i <- left until trLength) {
            trArrayEnd(i) = trArrayEnd(left - 1)
          }
          break
        }

        if (rawTrajectoryIndex - left >= 15){
          left = rawTrajectoryIndex
        }

        //就近填充方法
        //填充左半边
        for (j <- left until (left+(rawTrajectoryIndex-left)/2)) {
          trArrayEnd(j) = trArrayEnd(left - 1)
        }
        //填充右半边
        for (k <- (left + (rawTrajectoryIndex - left) / 2) until rawTrajectoryIndex) {
          trArrayEnd(k) = trArrayEnd(rawTrajectoryIndex)
        }

        /*
         //匀速填充方法
        //找出left-1 与 rawTrajectory 处 经纬度
        if (trArrayEnd(left-1) != null){
          val latAndLonLeft = trArrayEnd(left-1).split("@")
          val latLeft = latAndLonLeft(0).toDouble
          val lonLeft = latAndLonLeft(1).toDouble

          val latAndLonRawTrajectory = trArrayEnd(rawTrajectoryIndex).split("@")
          val latRawTrajectory = latAndLonRawTrajectory(0).toDouble
          val lonRawTrajectory = latAndLonRawTrajectory(1).toDouble

          //填充时采用匀速策略
          val gap = rawTrajectoryIndex - left
          val incrementLat = (latRawTrajectory-latLeft)/gap
          val incrementLng = (lonRawTrajectory-lonLeft)/gap
          var nextLat = latLeft
          var nextLng = lonLeft
          for (k <- left until rawTrajectoryIndex){
            nextLat += incrementLat
            nextLng += incrementLng
            trArrayEnd(k) = nextLat+"#"+nextLng
          }
        }*/


        left = rawTrajectoryIndex
      }
    }
    trArrayEnd
  }
}
