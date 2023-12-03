//package com.yx.DBSCANClustering;
//
//import com.yx.DBSCANClustering.trucks.Trucks_GDBSCAN;
//import com.yx.cellID.trucksGeoUtils.TrucksGrid;
//
//public class test {
//    public static void main(String[] args) {
//        Point[] points = new Point[13];
//        points[0] = new Point("a",0.1,0.5,9);
//        points[1] = new Point("b",0.1,0.6,9);
//        points[2] = new Point("c",0.1,0.7,9);
//        points[3] = new Point("d",0.1,0.8,9);
//        points[4] = new Point("e",2.1,1.2,9);
//        points[5] = new Point("f",2.1,1.2,9);
//        points[6] = new Point("g",2.1,1.3,9);
//        points[7] = new Point("h",2.1,1.3,9);
//        points[8] = new Point("i",3.1,3.1,9);
//        points[9] = new Point("j",3.1,3.1,9);
//        points[10] = new Point("k",3.1,3.1,9);
//        points[11] = new Point("l",3.1,3.1,9);
//        points[12] = new Point("m",0.1,3.1,9);
//
//        double eps = 1.0;
//
//
//        for (Point p : points){
//            p.setCellID(new TrucksGrid(eps).mapToGridCell(p.getX(),p.getY()));
//            System.out.println(p);
//        }
//
//        Trucks_GDBSCAN g_dbscan = new Trucks_GDBSCAN(eps, 3,points);
//        System.out.println(g_dbscan.cluster());
//
//
//    }
//}
