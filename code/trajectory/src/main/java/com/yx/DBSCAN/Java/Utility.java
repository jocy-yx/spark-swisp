package com.yx.DBSCAN.Java;

import scala.collection.mutable.ArrayBuffer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public final class Utility {

    
    public static double getDistance(Point p,Point q){
        double dx=p.getX()-q.getX();
        double dy=p.getY()-q.getY();
        double distance=Math.sqrt(dx*dx+dy*dy);
        return distance;
    }



   
    public static List<Point> isKeyPoint(List<Point> lst,Point p,double e,int minp){
        int count=0;
        List<Point> tmpLst=new ArrayList<Point>();
        for(Iterator<Point> it=lst.iterator();it.hasNext();){
            Point q=it.next();
            if(getDistance(p,q)<=e){
                ++count;
                if(!tmpLst.contains(q)){
                    tmpLst.add(q);
                }
            }
        }
        if(count>=minp){
            p.setKey(true);
            return tmpLst;
        }
        return null;
    }



    public static List<Point> getPointsList(Point[] dbscanArray) throws IOException{

        List<Point> list = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < dbscanArray.length ; i++) {
                list.add(dbscanArray[i]);
        }
        return list;//Arrays.asList(dbscanArray)

    }

}

