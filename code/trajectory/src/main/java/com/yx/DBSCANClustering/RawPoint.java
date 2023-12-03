package com.yx.DBSCANClustering;

public class RawPoint {
    private double x;
    private double y;
    private long cellID;

    public RawPoint(double x, double y, long cellID) {
        this.x = x;
        this.y = y;
        this.cellID = cellID;
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
}
