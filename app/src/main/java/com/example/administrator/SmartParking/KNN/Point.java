package com.example.administrator.SmartParking.KNN;

/**
 * Created by YasinZhang on 2018/3/9.
 */

public class Point {
    private long id;
    private double rx1;
    private double rx2;
    private double rx3;
    private double rx4;
    private double rx5;
    private double rx6;

    private double x;
    private double y;

    public Point(long id, double rx1, double rx2, double rx3, double rx4,
                 double rx5, double rx6, double x, double y) {
        this.id = id;
        this.rx1 = rx1;
        this.rx2 = rx2;
        this.rx3 = rx3;
        this.rx4 = rx4;
        this.rx5 = rx5;
        this.rx6 = rx6;
        this.x = x;
        this.y = y;
    }

    public Point(long id, double rx1, double rx2, double rx3,
                 double rx4, double rx5, double rx6) {
        this.id = id;
        this.rx1 = rx1;
        this.rx2 = rx2;
        this.rx3 = rx3;
        this.rx4 = rx4;
        this.rx5 = rx5;
        this.rx6 = rx6;
    }

    // get、set方法省略
    public long getId() {
        return id;
    }

    public double getR1() {
        return rx1;
    }

    public double getR2() {
        return rx2;
    }

    public double getR3() {
        return rx3;
    }

    public double getR4() {
        return rx4;
    }

    public double getR5() {
        return rx5;
    }

    public double getR6() {
        return rx6;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public void setRx(int i, double data) {
        switch (i) {
            case 1:
                this.rx1 = data;
                break;
            case 2:
                this.rx2 = data;
                break;
            case 3:
                this.rx3 = data;
                break;
            case 4:
                this.rx4 = data;
                break;
            case 5:
                this.rx5 = data;
                break;
            case 6:
                this.rx6 = data;
                break;
        }
    }
}
