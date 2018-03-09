package com.example.administrator.SmartParking.KNN;

/**
 * Created by YasinZhang on 2018/3/9.
 */

public class location {

    long id;
    private double x;
    private double y;

    public location(long id, double x, double y) {

        this.id = id;
        this.x = x;
        this.y = y;

    }

    public long getId() {
        return id;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;

    }

}
