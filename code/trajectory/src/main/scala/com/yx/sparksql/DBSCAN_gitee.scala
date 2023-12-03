package com.yx.sparksql

import org.apache.spark.sql.SparkSession

object DBSCAN_gitee {
  def main(args: Array[String]): Unit = {
   val spark = SparkSession
     .builder()
     .appName("dbscan")
     .master("local")
     .getOrCreate()

    val sc = spark.sparkContext
    import spark.implicits._



    // 1.找到核心点形成临时聚类簇
    // 该步骤一般要采用空间索引 + 广播的方法，此处从略，假定已经得到了临时聚类簇。

    //rdd_core的每一行代表一个临时聚类簇：(min_core_id, core_id_set)
    //core_id_set为临时聚类簇所有核心点的编号，min_core_id为这些编号中取值最小的编号
    var rdd_core = sc.parallelize(List((1L,Set(1L,2L)),(2L,Set(2L,3L,4L)),
      (6L,Set(6L,8L,9L)),(4L,Set(4L,5L)),
      (9L,Set(9L,10L,11L)),(15L,Set(15L,17L)),
      (10L,Set(10L,11L,18L))))
    //rdd_core.collect.foreach(println)





    // 2.合并临时聚类簇得到聚类簇
    import scala.collection.mutable.ListBuffer
    import org.apache.spark.HashPartitioner

    //定义合并函数：将有共同核心点的临时聚类簇合并
    val mergeSets = (set_list: ListBuffer[Set[Long]]) =>{
      var result = ListBuffer[Set[Long]]()
      while (set_list.size>0){
        var cur_set = set_list.remove(0)
        var intersect_idxs = List.range(set_list.size-1,-1,-1).filter(i=>(cur_set&set_list(i)).size>0)
        while(intersect_idxs.size>0){
          for(idx<-intersect_idxs){
            cur_set = cur_set|set_list(idx)
          }
          for(idx<-intersect_idxs){
            set_list.remove(idx)
          }
          intersect_idxs = List.range(set_list.size-1,-1,-1).filter(i=>(cur_set&set_list(i)).size>0)
        }
        result = result:+cur_set
      }
      result
    }

    ///对rdd_core分区后在每个分区合并，不断将分区数量减少，最终合并到一个分区
    //如果数据规模十分大，难以合并到一个分区，也可以最终合并到多个分区，得到近似结果。
    //rdd: (min_core_id,core_id_set)

    def mergeRDD(rdd: org.apache.spark.rdd.RDD[(Long,Set[Long])], partition_cnt:Int):
    org.apache.spark.rdd.RDD[(Long,Set[Long])] = {
      val rdd_merged =  rdd.partitionBy(new HashPartitioner(partition_cnt))
        .mapPartitions(iter => {
          val buffer = ListBuffer[Set[Long]]()
          for(t<-iter){
            val core_id_set:Set[Long] = t._2
            buffer.append(core_id_set)
          }
          val merged_buffer = mergeSets(buffer)
          var result = List[(Long,Set[Long])]()
          for(core_id_set<-merged_buffer){
            val min_core_id = core_id_set.min
            result = result:+(min_core_id,core_id_set)
          }
          result.iterator
        })
      rdd_merged
    }


    //分区迭代计算，可以根据需要调整迭代次数和分区数量
    rdd_core = mergeRDD(rdd_core,8)
    rdd_core = mergeRDD(rdd_core,4)
    rdd_core = mergeRDD(rdd_core,1)
    rdd_core.collect.foreach(println)

  }
}
