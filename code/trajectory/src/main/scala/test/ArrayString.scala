package test

object ArrayString {
  def main(args: Array[String]): Unit = {
    val trArrayEnd = new Array[String](3)
    trArrayEnd(0) = "1"
    trArrayEnd(2) = "2"

    for (i<- trArrayEnd){
      if (i == null) println("true")
    }

    val str = "1@2@3"
    if (str.contains("@")){
      println("yes")
    }



    val sb = new StringBuilder()
    sb.append(1)
    sb.append(2)
    println(sb.mkString("-"))



    var str2 = "123"
    val path = "1-2-3"
    if (!path.contains("*")){
      str2 = str2 + "*"
    }
  }
}
