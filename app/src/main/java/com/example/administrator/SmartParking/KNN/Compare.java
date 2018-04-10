package com.example.administrator.SmartParking.KNN;

import java.util.Comparator;

/**
 * Created by YasinZhang on 2018/3/9.
 */

public class Compare implements Comparator<Distance> {
    public int compare(Distance d1, Distance d2) {
        return d1.getDistance() > d2.getDistance() ? 20 : -1;
    }
}
