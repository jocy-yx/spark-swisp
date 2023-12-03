//package com.yx.sparksql
//
//import org.apache.spark.HashPartitioner
//import org.apache.spark.rdd.RDD
//import org.apache.spark.sql.SparkSession
//import org.apache.spark.storage.StorageLevel
//import org.apache.spark.sql.{Column, DataFrame, Row}
//import org.apache.spark.sql.functions._
//import org.locationtech.jts.geom.{Coordinate, Geometry, GeometryFactory, Point}
//import org.locationtech.jts.index.strtree.STRtree
//import org.apache.spark.sql.jts.registerTypes
//import org.apache.spark.sql.types.LongType
//
//import scala.collection.mutable.{ListBuffer, WrappedArray}
//import scala.collection.JavaConversions._
//import scala.collection.mutable
//
//object DBSCAN_Final {
//  def main(args: Array[String]): Unit = {
//    def printlog(info:String): Unit ={
//      val dt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date)
//      println("=========="*8+dt)
//      println(info+"\n")
//    }
//
//    val spark = SparkSession
//      .builder()
//      .appName("dbscan")
//      .enableHiveSupport()
//      .master("local")
//      .getOrCreate()
//
//    val sc = spark.sparkContext
//    import spark.implicits._
//    registerTypes
//
//
//
//
//
//    /*================================================================================*/
//    //  一，读入数据 dfinput
//    /*================================================================================*/
//    printlog("step1: get input data -> dfinput ...")
//
//    /*val dfdata = spark.read.option("header","true")//有头信息
//      .option("inferSchema","true")//自动推断类型
//      .option("delimiter", ",")//字段间用tab键分隔，注意只有两个字段（x,y）与经纬度还是有区别
//      .csv("E:\\大数据时空数据分布式伴随研究\\DBSCAN\\data.csv")//数据文件存放位置*/
//
//    val dfdata = spark.read.option("inferSchema",true)
//        .option("delimiter",";")
//        .csv("E:\\大数据时空数据分布式伴随研究\\数据集\\trucks\\trucks-新\\Trucks.txt")
//        .selectExpr("_c0","_c2","_c3","_c4","_c5")
//        .withColumnRenamed("_c0","id")
//        .withColumnRenamed("_c2","date")
//        .withColumnRenamed("_c3","time")
//        .withColumnRenamed("_c4","lat")
//        .withColumnRenamed("_c5","lon")
//        .selectExpr("id","concat(date,time) as time","lat","lon")
//        .select($"id".cast(LongType),$"time",$"lat",$"lon")
//
//
//    //dfdata.show(false)
//
//    spark.udf.register("makePoint", (x:Double,y:Double) =>{
//      val gf = new GeometryFactory
//      val pt = gf.createPoint(new Coordinate(x,y))
//      pt
//    })
//
////    val dfinput = dfdata.selectExpr("makePoint(lon,lat) as point")
////      .rdd.map(row=>row.getAs[Point]("point"))
////      .zipWithIndex().toDF("geometry","id").selectExpr("id","geometry")//自定义id，升序
////      .persist(StorageLevel.MEMORY_AND_DISK)
//
//    val dfinput0 = dfdata.selectExpr("id","makePoint(lon,lat) as geometry","time")
//
//
//    dfinput0.createOrReplaceTempView("test")
//
//    val dfinput =
//      spark.sql("select id,geometry from test where substring(time,1,15)='10/09/200209:15'")
//        .persist(StorageLevel.MEMORY_AND_DISK)
//
//
//
//
//    /*================================================================================*/
//    //  二，分批次广播RTree得到邻近关系 dfnear
//    /*================================================================================*/
//    printlog("step2: looking for neighbours by broadcasting Rtree -> dfnear ...")
//
//    spark.udf
//      .register("getBufferBox", (p: Point) => p.getEnvelope.buffer(0.055).getEnvelope)//*********
//
//    //分批次进行广播
//    val partition_cnt = 5//10
//    val rdd_input = dfinput.rdd.repartition(10).persist(StorageLevel.MEMORY_AND_DISK)//20
//    val dfbuffer = dfinput.selectExpr("id","getBufferBox(geometry) as envelop").repartition(partition_cnt)
//    var dfnear = List[(Long,Long,Point)]().toDF("s_fid","m_fid","s_geom")
//
//
//    for(partition_id <- 0 until partition_cnt){
//      val bufferi = dfbuffer.rdd.mapPartitionsWithIndex(
//        (idx, iter) => if (idx == partition_id ) iter else Iterator())
//      val Rtree = new STRtree()
//      bufferi.collect.foreach(x => Rtree.insert(x.getAs[Geometry]("envelop").getEnvelopeInternal, x))
//      val tree_broads = sc.broadcast(Rtree)
//
//      val dfneari = rdd_input.mapPartitions(iter => {
//        var res_list = List[(Long,Long,Point)]()//s_fid,m_fid,s_geom
//        val tree = tree_broads.value
//        for (cur<-iter) {
//          val s_fid = cur.getAs[Long]("id")
//          val s_geom = cur.getAs[Point]("geometry")
//          val results = tree.query(s_geom.getEnvelopeInternal).asInstanceOf[java.util.List[Row]]
//
//          for (x<-results) {
//            val m_fid = x.getAs[Long]("id")
//            val m_envelop = x.getAs[Geometry]("envelop")
//            if(m_envelop.intersects(s_geom)){
//              res_list = res_list:+(s_fid,m_fid,s_geom)
//            }
//          }
//        }
//        res_list.iterator
//      }).toDF("s_fid","m_fid","s_geom")
//
//      dfnear = dfnear.union(dfneari)
//    }
//
//    dfnear.show(10,truncate = false)
//
//    println(dfnear.count())
//
//
//
//
//    /*================================================================================*/
//    //  三，根据DBSCAN邻域半径得到有效邻近关系 dfpair
//    /*================================================================================*/
//    printlog("step3: looking for effective pairs by DNN model-> dfpair...")
//
//
//
//    val dfpair_raw = dfinput.join(dfnear, dfinput("id")===dfnear("m_fid"), "right")
//      .selectExpr("s_fid","m_fid","s_geom","geometry as m_geom")
//
//    spark.udf.register("distance", (p: Point, q:Point) => p.distance(q))
//    val dfpair = dfpair_raw.where("distance(s_geom,m_geom) < 0.055") //邻域半径R设置为0.2 *******************
//      .persist(StorageLevel.MEMORY_AND_DISK)
//
//
//
//
//
//    /*================================================================================*/
//    //  四，创建临时聚类簇 dfcore
//    /*================================================================================*/
//    printlog("step4: looking for temporatory clusters -> dfcore ...")
//
//
//    val dfcore = dfpair.groupBy("s_fid").agg(
//      first("s_geom") as "s_geom",
//      count("m_fid") as "neighbour_cnt",
//      collect_list("m_fid") as "neighbour_ids"
//    ).where("neighbour_cnt>=5")  //此处最少点数目minpoits设置为20  *************************
//      .persist(StorageLevel.MEMORY_AND_DISK)
//
//
//
//
//
//    /*================================================================================*/
//    //  五，得到临时聚类簇的核心点信息  rdd_core
//    /*================================================================================*/
//    printlog("step5: get infomation for temporatory clusters -> rdd_core ...")
//
//    val dfpair_join = dfcore.selectExpr("s_fid").join(dfpair,Seq("s_fid"),"inner")
//    val df_fids = dfcore.selectExpr("s_fid as m_fid")
//    val dfpair_core = df_fids.join(dfpair_join,Seq("m_fid"),"inner")
//    var rdd_core = dfpair_core.groupBy("s_fid").agg(
//      min("m_fid") as "min_core_id",
//      collect_set("m_fid") as "core_id_set"
//    ).rdd.map(row =>{
//      val min_core_id = row.getAs[Long]("min_core_id")
//      val core_id_set = row.getAs[WrappedArray[Long]]("core_id_set").toArray.toSet
//      (min_core_id,core_id_set)
//    })
//
//
//
//
//
//
//    /*================================================================================*/
//    //  六，对rdd_core分区分步合并  rdd_core(min_core_id, core_id_set)
//    /*================================================================================*/
//    printlog("step6: run dbscan clustering ...")
//
//    //定义合并方法
//    val mergeSets = (set_list: ListBuffer[Set[Long]]) =>{
//      var result = ListBuffer[Set[Long]]()
//      while (set_list.size>0){
//        var cur_set = set_list.remove(0)
//        var intersect_idxs = List.range(set_list.size-1,-1,-1).filter(i=>(cur_set&set_list(i)).size>0)
//        while(intersect_idxs.size>0){
//          for(idx<-intersect_idxs){
//            cur_set = cur_set|set_list(idx)
//          }
//          for(idx<-intersect_idxs){
//            set_list.remove(idx)
//          }
//          intersect_idxs = List.range(set_list.size-1,-1,-1).filter(i=>(cur_set&set_list(i)).size>0)
//        }
//        result = result:+cur_set
//      }
//      result
//    }
//
//    //对rdd_core分区后在每个分区合并，不断将分区数量减少，最终合并到一个分区
//    //如果数据规模十分大，难以合并到一个分区，也可以最终合并到多个分区，得到近似结果。
//    //rdd: (min_core_id,core_id_set)
//
//    def mergeRDD(rdd: org.apache.spark.rdd.RDD[(Long,Set[Long])], partition_cnt:Int):
//    org.apache.spark.rdd.RDD[(Long,Set[Long])] = {
//      val rdd_merged =  rdd.partitionBy(new HashPartitioner(partition_cnt))
//        .mapPartitions(iter => {
//          val buffer = ListBuffer[Set[Long]]()
//          for(t<-iter){
//            val core_id_set:Set[Long] = t._2
//            buffer.add(core_id_set)
//          }
//          val merged_buffer = mergeSets(buffer)
//          var result = List[(Long,Set[Long])]()
//          for(core_id_set<-merged_buffer){
//            val min_core_id = core_id_set.min
//            result = result:+(min_core_id,core_id_set)
//          }
//          result.iterator
//        })
//      rdd_merged
//    }
//
//
//    //!此处需要调整分区数量和迭代次数
//    for(pcnt<-Array(4,1)){  //Array(16,8,4,1)
//      rdd_core = mergeRDD(rdd_core,pcnt)
//    }
//
//
//
//
//
//    /*================================================================================*/
//    //  七，获取每一个core的簇信息
//    /*================================================================================*/
//    printlog("step7: get cluster ids ...")
//
//    val dfcluster_ids = rdd_core.flatMap(t => {
//      val cluster_id = t._1
//      val id_set = t._2
//      for(core_id<-id_set) yield (cluster_id, core_id)
//    }).toDF("cluster_id","s_fid")
//
//    val dfclusters =  dfcore.join(dfcluster_ids, Seq("s_fid"), "left")
//
//
//
//
//
//
//    /*================================================================================*/
//    //  八，求每一个簇的代表核心和簇元素数量
//    /*================================================================================*/
//    printlog("step8: evaluate cluster representation ...")
//
//    val rdd_cluster = dfclusters.rdd.map(row=> {
//      val cluster_id = row.getAs[Long]("cluster_id")
//      val s_geom = row.getAs[Point]("s_geom")
//      val neighbour_cnt = row.getAs[Long]("neighbour_cnt")
//      val id_set = row.getAs[WrappedArray[Long]]("neighbour_ids").toSet
//      (cluster_id,(s_geom,neighbour_cnt,id_set))
//    })
//
//    val rdd_result = rdd_cluster.reduceByKey((a,b)=>{
//      val id_set = a._3 | b._3
//      val result = if(a._2>=b._2) (a._1,a._2,id_set)
//      else (b._1,b._2,id_set)
//      result
//    })
//
//    val dfresult = rdd_result.map(t=>{
//      val cluster_id = t._1
//      val representation_point = t._2._1
//      val neighbour_points_cnt = t._2._2
//      val id_set = t._2._3.toSeq
//      val cluster_points_cnt = id_set.size
//      (cluster_id,representation_point,neighbour_points_cnt,cluster_points_cnt,id_set)
//    }).toDF("cluster_id","representation_point","neighbour_points_cnt","cluster_points_cnt","id_set")
//
//
//    dfresult.show(10,truncate = false)
//
//
//    spark.udf.register("myudf",(arg1:Int,arg2:Seq[Int])=>{
//      val array = new Array[String](arg2.length)
//      for (i <- 0 until arg2.length){
//        array(i) = "2021MMddhhmm,"+arg1+","+arg2(i)
//      }
//      array.mkString("_")
//    })
//
//    val df = dfresult.selectExpr("myudf(cluster_id,id_set) as c")
//      .flatMap(fun => {
//        val strings = fun.getString(0).split("_")
//        strings })
//
//    df.show(false)
//
//
//    spark.stop()
//
//  }
//}
