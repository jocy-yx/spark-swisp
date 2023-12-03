package com.yx.DBSCANClustering;

import java.util.HashSet;

public class SimpleCluster {
    private HashSet<String> oids;//对象的集合
    private String ID;//簇ID


    // 无参构造方法
    public SimpleCluster() {
//	oids = new HashSet<Integer>();
        oids = new HashSet<String>();
    }


    public void setID(String ID) {
        this.ID = ID;
    }


    // 在簇中增加对象
    public void addObject(String obj) {
        oids.add(obj);
    }

    public HashSet<String> getOids() {
        return oids;
    }

    public String getID() {
        return ID;
    }

    // 簇的toString方法
    @Override
    public String toString() {
        return "<" + ID + ":" + oids.toString() + ">";
    }
}
