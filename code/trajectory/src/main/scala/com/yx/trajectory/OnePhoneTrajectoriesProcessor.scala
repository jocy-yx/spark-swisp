package com.yx.trajectory


import com.yx.spark.trajectory.{RawTrajectory, TrajectoryData}

class OnePhoneTrajectoriesProcessor(time:String,clusterID:Int,lon:Double,lat:Double) {

  /**
    * 生成当前这个用户phone下的所有轨迹与基站ID映射
    */
  def buildRawTrajectory(): RawTrajectory = {
    //本质就是生成RawTrajectory

    val phone = new RawTrajectory()
    val timeString = time
    val hour = timeString.substring(0, 2).toInt
    val minute = timeString.substring(3, 5).toInt
    val ts = hour * 60 + minute
    phone.setTimeStamp(hour * 60 + minute)

    //计算id
    val latAndLon = ts+"T"+clusterID


    phone.setBaseId(latAndLon)

    phone //返回值

  }
}
