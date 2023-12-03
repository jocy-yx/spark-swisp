package com.yx.DBSCANClustering.trucks;


import com.yx.DBSCANClustering.FinalPoint;
import com.yx.DBSCANClustering.Point;
import com.yx.DBSCANClustering.RawPoint;
import com.yx.DBSCANClustering.SimpleCluster;
import com.yx.cellID.trucksGeoUtils.TrucksGrid;

import java.util.*;

public class Trucks_GDBSCAN {

    private static int ID_COUNTER = 0;//cluster簇的标识

    // 点的状态 （枚举类型）
    private enum PointStatus {
        // 这一点被认为是噪音。
        NOISE,
        // 这个点已经是cluster的一部分。
        PART_OF_CLUSTER
    }

    private double eps;//密度阈值
    private double minPts;//显著性阈值
    private HashMap<String,RawPoint> rawData = new HashMap<>();
    //++++++++++
    private HashMap<Long, List<String>> cellAndUsers = new HashMap<>();
    //++++++++++

    // 构造方法2
    public Trucks_GDBSCAN(double eps, int minPts, Point[] points) {
        this.eps = eps;
        this.minPts = minPts;
        for (Point p :points){
            if (!rawData.containsKey(p.getUserID())){
                rawData.put(p.getUserID(),new RawPoint(p.getX(),p.getY(),p.getCellID()));
                //++++++++++
                long cellID = p.getCellID();
                if (!cellAndUsers.containsKey(cellID)){
                    List<String> oids = new ArrayList<>();
                    oids.add(p.getUserID());
                    cellAndUsers.put(cellID,oids);
                }else{
                    List<String> oids = cellAndUsers.get(cellID);
                    oids.add(p.getUserID());
                    cellAndUsers.put(cellID,oids);
                }
                //++++++++++
            }
        }
        //System.out.println(cellAndUsers);
    }


    //执行 DBSCAN聚类 分析（注意此处有Spark元素）
    public ArrayList<FinalPoint> cluster() {
//	ArrayList<Integer> noisies = new ArrayList<>();
        ArrayList<SimpleCluster> clusters = new ArrayList<>();
        Map<String, PointStatus> visited = new HashMap<>();
        for (String point : rawData.keySet()) {
            if (visited.get(point) != null) {
                continue;
            }
            ArrayList<String> neighbors = getNeighbors(point);
            if (neighbors.size() >= minPts) {
                // DBSCAN does not care about center points
                //String id = SparkEnv.get().executorId();
//        	SimpleCluster cluster = new Cluster(sp);
                SimpleCluster cluster = new SimpleCluster();
                //set the global ID of this cluster
                //cluster.setID(id + "00" + (ID_COUNTER++));
                cluster.setID("" + (ID_COUNTER++));
                clusters.add(expandCluster(cluster, point, neighbors, visited));
            } else {
                visited.put(point, PointStatus.NOISE);
//                noisies.add(point);
            }
        }
//        clusters.add(noisies);
        //return clusters;

        ArrayList<FinalPoint> finalPoints = new ArrayList<>();
        for (SimpleCluster simpleCluster:clusters){
            for (String oid : simpleCluster.getOids()){
                finalPoints.add(new FinalPoint(
                        oid,rawData.get(oid).getX(),rawData.get(oid).getY(),
                        simpleCluster.getID()));
            }
        }
        return finalPoints;
    }


    // 扩展簇以包含密度可达的项目
    private SimpleCluster expandCluster(SimpleCluster cluster,
                                        String point,
                                        ArrayList<String> neighbors,
                                        Map<String, PointStatus> visited) {
        cluster.addObject(point);
        visited.put(point, PointStatus.PART_OF_CLUSTER);

        ArrayList<String> seeds = new ArrayList<String>(neighbors);
        int index = 0;
        while (index < seeds.size()) {
            String current = seeds.get(index);
            PointStatus pStatus = visited.get(current);
            // only check non-visited points
            if (pStatus == null) {
                ArrayList<String> currentNeighbors = getNeighbors(current);
                if (currentNeighbors.size() >= minPts) {
                    seeds = merge(seeds, currentNeighbors);
                }
            }

            if (pStatus != PointStatus.PART_OF_CLUSTER) {
                visited.put(current, PointStatus.PART_OF_CLUSTER);
                cluster.addObject(current);
            }
            index++;
        }
        return cluster;
    }


    // 合并两个列表
    private ArrayList<String> merge(ArrayList<String> one, ArrayList<String> two) {
        HashSet<String> oneSet = new HashSet<String>(one);
        for (String item : two) {
            if (!oneSet.contains(item)) {
                one.add(item);
            }
        }
        return one;
    }


    // 返回point的邻域点
    private ArrayList<String> getNeighbors(String point) {
        ArrayList<String> neighbors = new ArrayList<String>();

        //++++++++++
        List<String> pointsList2 = new ArrayList<>();
        TrucksGrid trucks_gdbscan = new TrucksGrid(eps);
        List<Long> closeIds = trucks_gdbscan.closeId(rawData.get(point).getCellID());
        for (long cellID : closeIds){
            List<String> closePoints = cellAndUsers.get(cellID);
            if (closePoints != null){
                pointsList2.addAll(closePoints);
            }
        }
        //++++++++++

        for (String neighbor : pointsList2) {//rawData.keySet()//pointsList2
            if (!point.equals(neighbor) && dist(neighbor, point) <= eps) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }



    // 计算两点之间的距离
    private double dist(String neighbor, String point) {
        RawPoint p1 = rawData.get(neighbor);
        RawPoint p2 = rawData.get(point);
        if (p1 == null || p2 == null) {
            return Double.MAX_VALUE;
        }
        double x1 = p1.getX(), y1 = p1.getY();
        double x2 = p2.getX(), y2 = p2.getY();
        double xdiff = Math.abs(x2 - x1);
        double ydiff = Math.abs(y2 - y1);
        return Math.sqrt(xdiff * xdiff + ydiff * ydiff) ;
    }

}
