package com.yx.DBSCANClustering;

public class Point {
    private String userID;
    private double x;
    private double y;
    private long cellID;


    public Point(String userID, double x, double y, long cellID) {
        this.userID = userID;
        this.x = x;
        this.y = y;
        this.cellID = cellID;
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

    public long getCellID() {
        return cellID;
    }

    public void setCellID(int cellID) {
        this.cellID = cellID;
    }

    @Override
    public String toString() {
        return "Point{" +
                "userID='" + userID + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", cellID=" + cellID +
                '}';
    }
}
