package com.yx.DBSCANClustering;

public class FinalPoint {
    private String userID;
    private double x;
    private double y;
    private String clusterID;

    public FinalPoint(String userID, double x, double y, String clusterID) {
        this.userID = userID;
        this.x = x;
        this.y = y;
        this.clusterID = clusterID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getClusterID() {
        return clusterID;
    }

    public void setClusterID(String clusterID) {
        this.clusterID = clusterID;
    }

    @Override
    public String toString() {
        return "FinalPoint{" +
                "userID='" + userID + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", clusterID='" + clusterID + '\'' +
                '}';
    }
}
