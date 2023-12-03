package com.yx.DBSCAN.Java;


public class Point {
    private String user;
    private double x;
    private double y;
    private boolean isKey=false;
    private boolean isClassed;
    private int cellID;
    private int clusterID;

    public boolean isKey() {
        return isKey;
    }
    public void setKey(boolean isKey) {
        this.isKey = isKey;
        this.isClassed=true;
    }
    public boolean isClassed() {
        return isClassed;
    }
    public void setClassed(boolean isClassed) {
        this.isClassed = isClassed;
    }
    public double getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public void setCellID(int cellID) {
        this.cellID = cellID;
    }

    public int getCellID() {
        return cellID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getClusterID() {
        return clusterID;
    }

    public void setClusterID(int clusterID) {
        this.clusterID = clusterID;
    }

    public Point(){
        x=0;
        y=0;
    }
    public Point(double x,double y){
        this.x=x;
        this.y=y;
    }

    public Point(String user, double x, double y) {
        this.user = user;
        this.x = x;
        this.y = y;
    }

    public Point(String user,int cellID, double x, double y) {
        this.user = user;
        this.cellID = cellID;
        this.x = x;
        this.y = y;
    }

    public Point(String str){
        String[] p=str.split(",");
        this.user = p[0];
        this.x=Double.parseDouble(p[1]);
        this.y=Double.parseDouble(p[2]);
    }


    @Override
    public String toString() {
        return "{"+this.x+","+this.y+","+this.user+","+this.clusterID+"}";
    }
}

