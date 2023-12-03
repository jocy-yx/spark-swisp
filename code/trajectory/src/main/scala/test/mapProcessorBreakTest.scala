package test

object mapProcessorBreakTest {
  def main(args: Array[String]): Unit = {

    var rawTrajectoryIndex = 0

    import scala.util.control.Breaks._
    breakable {
      while (rawTrajectoryIndex < 5) {

        if (rawTrajectoryIndex - 3 == 1) {
          break
        }
        rawTrajectoryIndex += 1
        break
      }
    }

    println(rawTrajectoryIndex)

  }
}
