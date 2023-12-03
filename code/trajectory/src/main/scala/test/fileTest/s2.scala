//package test.fileTest
//
//import com.google.common.geometry.{S2CellId, S2LatLng}
//
//object s2 {
//  def main(args: Array[String]): Unit = {
//    val str ="38113080,20210531055622,85,123.829697,42.294549"
//
//    val array = str.split(",")
//    val lat = array(4).toDouble//纬度
//    val lng = array(3).toDouble//经度
//
//
//    val s2LatLng : S2LatLng = S2LatLng.fromDegrees(lat, lng)
//    val celId = S2CellId.fromLatLng(s2LatLng).parent(10)
//
//    println(celId.id().toString)
//  }
//}
