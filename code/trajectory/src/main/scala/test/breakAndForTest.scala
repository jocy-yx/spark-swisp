package test

import scala.util.control.Breaks._
import collection.JavaConversions._

object breakAndForTest {
  def main(args: Array[String]): Unit = {

    println("1.scala 实现continue")
    // 这里与上面的区别是将if-else语句放置在breakable方法内部，而没有将整个循环结构放置在方法内部
    // 这样做可以实现结束本次执行而不是整个循环结束，从而实现continue功能
    for(i <- 1 to 10){
      println(i)
      breakable{
        if(i == 3){
          break
        }
        println(i)
      }
    }

    println("2.scala 实现break")

    // breakable方法与break方法组合使用实现break功能
    // 将整个循环放置于breakable方法中，然后需要跳出循环的时候则使用break方法，则跳出整个循环
    breakable({
      for(j <- 1 to 10) {
        if (j == 3) {
          break
        }
        println(j)
      }
    })

    println("3.scala 同时实现break 和 continue")

    // breakable包含整个for循环，允许break
    breakable({
      for(i <- 1 to 10){
        println("|--------|")
        // breakable包含如下if结构，允许continue
        breakable({
          if(i == 4){
            break()
          }
          println(i)

        // 如下if结构不被breakable包含，允许break
        if(i == 8){
          break()
        }
        })
      }
    })
  }

}
