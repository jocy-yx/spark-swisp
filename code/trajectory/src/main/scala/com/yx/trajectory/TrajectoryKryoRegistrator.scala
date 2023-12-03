package com.yx.trajectory

import com.esotericsoftware.kryo.Kryo
import com.yx.spark.trajectory.{Bucket, RawTrajectory, TrajectoryData}
import org.apache.spark.serializer.KryoRegistrator

class TrajectoryKryoRegistrator extends KryoRegistrator {
  override def registerClasses(kryo: Kryo): Unit = {
    kryo.register(classOf[TrajectoryData])
    kryo.register(classOf[RawTrajectory])
    kryo.register(classOf[Bucket])
    kryo.register(classOf[Finished])
  }
}

