package test

import scala.collection.mutable

object splitLength {
  def main(args: Array[String]): Unit = {
    val str = "123-123-123-125-125"
    println(str.split("-").length)


    val GeoHashSplit = str.split("-")
    val GeoHashSet = mutable.Set(GeoHashSplit(0))
    var beforeF = true
    val beforeG = GeoHashSplit(0).substring(0,2)//3位写2；7位写6
   /* var afterF = true
    val afterG = GeoHashSplit(0).substring(2)//3位写2；7位写6*/
    for (i <- 1 until GeoHashSplit.length){
      GeoHashSet += GeoHashSplit(i)
    }

    import scala.util.control.Breaks._
    breakable{
      for (i <- 1 until GeoHashSplit.length){
        if (!GeoHashSplit(i).substring(0,2).equals(beforeG)){//3位写2；7位写6
          beforeF = false
          break
        }
      }
    }


    if (GeoHashSet.size == 2 && beforeF){
      println("需要删除")
    }




  }
}
