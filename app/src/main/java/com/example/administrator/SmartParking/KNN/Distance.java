package com.example.administrator.SmartParking.KNN;

/**
 * Created by YasinZhang on 2018/3/9.
 */

public class Distance {
    // 已知点id
    private long id;
    // 未知点id
    private long nid;
    // 二者之间的距离
    private double distance;

    public Distance(long id, long nid, double distance) {
        this.id = id;
        this.nid = nid;
        this.distance = distance;
    }

    public long getId() {
        return id;
    }

    public long getnid() {
        return nid;
    }

    public double getDistance() {
        return distance;
    }
}
