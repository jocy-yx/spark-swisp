package com.yx.trajectory.Utils

import scala.collection.mutable


object ResultDataPro {

  def pro(str: String): Boolean = {
    val GeoHashSplit = str.split("-")
    var negativeF = true
    val GeoHashSet = mutable.Set(GeoHashSplit(0))
    /*if (GeoHashSplit(0).equals("*")) {
      negativeF = false
    }*/
    for (i <- 1 until GeoHashSplit.length) { //找出只有两个不同的基站结果
      GeoHashSet += GeoHashSplit(i)
      /*if (GeoHashSplit(i).equals("*")) {
        negativeF = false
      }*/
    }

    //找出只是最后一个基站不同的情况
    var diffF = true
    for (j <- 1 until GeoHashSplit.length - 1) {
      if (!GeoHashSplit(j).equals(GeoHashSplit(0))) {
        diffF = false
      }
    }


    if (!negativeF || (GeoHashSet.size == 2 && GeoHashSplit.length>2)||diffF) {
      false// 不要
    } else {
      true//要
    }

  }
}
