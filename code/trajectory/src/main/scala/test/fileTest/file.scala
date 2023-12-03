package test.fileTest

import java.io._
import java.nio.file.Files
import java.nio.file.Paths


object file {
  def main(args: Array[String]): Unit = {
    val path = "data/output/text.txt"
    val count =Files.lines(Paths.get(path)).count+1
    val writeFile = new File(path)
    val inputString = "I"+"\t"+"love"+"\t"+"you"
    val writer = new BufferedWriter(new FileWriter(writeFile))
    writer.write(inputString)
    writer.close()
  }
}
