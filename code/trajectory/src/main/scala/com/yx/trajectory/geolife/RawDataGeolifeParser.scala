package com.yx.trajectory.geolife

import com.yx.cellID.geolife.GeoLifeGrid
import com.yx.spark.trajectory.TrajectoryData

//原始数据是字符串类型，想要操作原始数据，必须完成字符串类型的数据的转换，转换为Java对象。

object RawDataGeolifeParser {

  //解析的工具方法，将原始数据中的每一行转为TrajectoryRaw对象
  //原始数据的每一行数据格式：0862;1;10/09/2002;09:15:59;23.845089;38.01847;486253.80;4207588.10
  //如果原始数据第一行是一个提示信息，需要进行过滤处理
  def parse(line:String,eps:Double):Option[TrajectoryData]={
    val fields = line.split(",") //对每一行数据，按照分隔符进行切分
//    val fields = line.split(",") //对每一行数据，按照分隔符进行切分
    if (fields.length == 5) {
      val trajectoryData = new TrajectoryData()

      val grid = new GeoLifeGrid(eps)
      val cellID = grid.mapToGridCell(fields(1).toDouble,fields(0).toDouble)

      trajectoryData.setPhone(fields(2))
      trajectoryData.setDateTime(fields(3)+fields(4))
      trajectoryData.setConnectionType(cellID.toString)//网格ID
      trajectoryData.setLon(fields(1))//经度
      trajectoryData.setLat(fields(0))//纬度
      Some(trajectoryData)
    }else{
      None
    }
  }

}
