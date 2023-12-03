//package test
//
//
//
//import scala.collection.mutable
//
//object pathTest {
//  def main(args: Array[String]): Unit = {
//    //val strSort = "95+20677-20677|95+20677-20677-20677|96+20677-20677|96+20677-20677-20677|"
//    //val strn = "95+20677-20677-20677|96+20677-20677-94951|"
//    //val strSort = "95+20677-20677|95+20677-20677-20677-94951-94951-20677|96+20677-20677|96+20677-20677-94951-94951-20677-20677|197+20677-94951|"
//
//    val strSort = "103+34861-56804-34861-34861-34861-34861|107+34861-34861-34861-34861-56804-34861|108+34861-34861-34861-56804-34861|109+34861-34861-56804-34861|110+34861-56804-34861|"
//
//    val longPathMap = mutable.Map[Int, String]()
//    val arr = strSort.split("\\|")
//
//    if (arr.length == 1) {
//      val arrSingle = arr(0).split("\\+")
//      longPathMap += (arrSingle(0).toInt -> arrSingle(1))
//    } else {
//      val arrSingleFirst = arr(0).split("\\+")
//      longPathMap += (arrSingleFirst(0).toInt -> arrSingleFirst(1))
//      for (i <- 1 until arr.length) {
//        val arrSingle = arr(i).split("\\+")
//        if ((arrSingle(0) == arr(i - 1).split("\\+")(0)
//          && arrSingle(1).length > arr(i - 1).split("\\+")(1).length)
//          || arrSingle(0) != arr(i - 1).split("\\+")(0)) {
//          longPathMap += (arrSingle(0).toInt -> arrSingle(1))
//        }
//      }
//    }
//
//
//
//    val strResult = new mutable.StringBuilder()
//    val mapSort = longPathMap.toSeq.sortBy(_._1)
//    for ((key, value) <- mapSort) {
//      strResult.append(key + "+" + value + "|")
//    }
//
//    println(strResult.toString())
//
//  }
//}
