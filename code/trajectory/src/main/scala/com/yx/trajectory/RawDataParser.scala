package com.yx.trajectory

import com.yx.spark.trajectory.TrajectoryData

//原始数据是字符串类型，想要操作原始数据，必须完成字符串类型的数据的转换，转换为Java对象。

object RawDataParser {

  //解析的工具方法，将原始数据中的每一行转为TrajectoryRaw对象
  //原始数据的每一行数据格式：381130802021053105562285123.82969742.294549
  //如果原始数据第一行是一个提示信息，需要进行过滤处理
  def parse(line:String):Option[TrajectoryData]={
    val fields = line.split(";") //对每一行数据，按照分隔符进行切分
//    val fields = line.split(",") //对每一行数据，按照分隔符进行切分
    if (fields.length == 8) {
      val trajectoryData = new TrajectoryData()
      trajectoryData.setPhone(fields(0))
      trajectoryData.setDateTime(fields(2)+fields(3))
      trajectoryData.setConnectionType(fields(1))
      trajectoryData.setLon(fields(5))//经度
      trajectoryData.setLat(fields(4))//纬度
      Some(trajectoryData)
    }else{
      None
    }
  }

}
