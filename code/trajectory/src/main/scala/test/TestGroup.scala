package test

import org.apache.spark.{SparkConf, SparkContext}

object TestGroup {
  def main(args: Array[String]): Unit = {
    //Spark应用的基础配置
    val conf = new SparkConf()
    conf.setAppName("TestGroup")
    conf.setMaster("local")//本地运行模式，此处没有放到集群上运行

    //开启kryo序列化，开启之后序列化的事情就不用管了
    conf.set("spark.serializer","org.apache.spark.serializer.KryoSerializer")
    val sc = new SparkContext(conf)

    val pairRDD = sc.parallelize(Seq(("a","1"),("b","2"),("c","3"),("a","3")))

    val resultRDD = pairRDD.reduceByKey(_+_)

    val mapTrajectoryOutputPath = "data/output/group"
    resultRDD.saveAsTextFile(mapTrajectoryOutputPath)
  }
}
