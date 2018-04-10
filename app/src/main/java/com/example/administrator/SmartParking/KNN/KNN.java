package com.example.administrator.SmartParking.KNN;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by YasinZhang on 2018/3/9.
 */

public class KNN {
    /**
     * 算法如下：
     * 1、输入所有已知点
     * 2、输入未知点
     * 3、计算所有已知点到未知点的欧式距离
     * 4、根据距离对所有已知点排序
     * 5、选出距离未知点最近的k个点
     * 6、计算k个点所在分类出现的频率
     * 7、选择频率最大的类别即为未知点的类别
     */

    public static location KNN_GetLocation(Point x) {

        // 一、输入所有已知点
        List<Point> dataList = createDataSet();

        // 二、输入未知点
        //Point x = new Point(3, -57, -56, -44, -47, -36, -46, 0.485, 0.93);

        // 三、计算所有已知点到未知点的欧式距离，并根据距离对所有已知点排序
        Compare compare = new Compare();
        Set<Distance> distanceSet = new TreeSet<>(compare);
        for (Point point : dataList) {
            distanceSet.add(new Distance(point.getId(), x.getId(), oudistance(point, x)));
        }
        // 四、选取最近的k个点
        double k = 2;

        List<Distance> distanceList = new ArrayList<>(distanceSet);
        //location loc = getlocation(distanceList, dataList, k, x); // 返回预测坐标

        /*// 计算误差
        double error = Math.pow(loc.getX() - x.getX(), 2) + Math.pow(loc.getY() - x.getY(), 2);
        error = Math.sqrt(error);
        System.out.println("误差为：" + error);*/

        return getlocation(distanceList, dataList, k, x);
    }

    // 欧式距离计算
    private static double oudistance(Point point1, Point point2) {
        double temp = Math.pow(point1.getR1() - point2.getR1(), 2) + Math.pow(point1.getR2() - point2.getR2(), 2)
                + Math.pow(point1.getR3() - point2.getR3(), 2) + Math.pow(point1.getR4() - point2.getR4(), 2)
                + Math.pow(point1.getR5() - point2.getR5(), 2) + Math.pow(point1.getR6() - point2.getR6(), 2);
        return Math.sqrt(temp);
    }

    // 计算每个分类包含的点的个数
    private static location getlocation(List<Distance> listDistance, List<Point> listPoint, double k, Point x) {
        int i = 0;
        long id;
        double distance_All = 0;
        double x_All = 0;
        double y_All = 0;
        double x_Average = 0;
        double y_Average = 0;
        // System.out.println("选取的k个点，由近及远依次为：");
        for (Distance distance : listDistance) {
            if (distance.getDistance() < 10) {
                //System.out.println("id为" + distance.getId() + ",距离为：" + distance.getDistance());

                // 通过id找到所属类型,找到对应坐标
                id = distance.getId();

                for (Point point : listPoint) {
                    if (point.getId() == id) {
                        x_All = x_All + point.getX() / (distance.getDistance() + 0.001);
                        y_All = y_All + point.getY() / (distance.getDistance() + 0.001);
                    }
                }
                distance_All = distance_All + 1 / (distance.getDistance() + 0.001);
            }
            i++;
            if (i >= k)
                break;
        }

        if (x_All == 0 || y_All == 0) {
            id = listDistance.get(0).getId();
            for (Point point : listPoint) {
                if (point.getId() == id) {
                    x_Average = point.getX();
                    y_Average = point.getY();
                }
            }
        } else {
            x_Average = x_All / distance_All;
            y_Average = y_All / distance_All;
        }
        //System.out.println("坐标为：[" + x_Average + "," + y_Average + "]");

        return new location(x.getId(), x_Average, y_Average);

    }

    private static ArrayList<Point> createDataSet() {

        Point point1 = new Point(1, -58, -40, -56, -49, -47, -44, 0, 0.93);
        Point point2 = new Point(2, -48, -47, -43, -41, -49, -39, 0.97, 0.93);
        Point point3 = new Point(3, -57, -53, -61, -49, -51, -52, 1.94, 0.93);

        Point point4 = new Point(4, -49, -50, -47, -48, -43, -52, 0, 1.86);
        Point point5 = new Point(5, -51, -49, -41, -41, -47, -45, 0.97, 1.86);
        Point point6 = new Point(6, -47, -52, -48, -43, -58, -42, 1.94, 1.86);

        Point point7 = new Point(7, -44, -57, -43, -41, -46, -45, 0, 2.79);
        Point point8 = new Point(8, -53, -46, -52, -34, -63, -46, 0.97, 2.79);
        Point point9 = new Point(9, -55, -43, -44, -52, -42, -51, 1.94, 2.79);

        Point point10 = new Point(10, -44, -44, -44, -41, -44, -48, 0, 3.72);
        Point point11 = new Point(11, -37, -41, -51, -39, -45, -46, 0.97, 3.72);
        Point point12 = new Point(12, -38, -45, -36, -47, -50, -46, 1.94, 3.72);

        Point point13 = new Point(13, -41, -51, -39, -42, -48, -49, 0, 4.65);
        Point point14 = new Point(14, -42, -41, -50, -40, -49, -59, 0.97, 4.65);
        Point point15 = new Point(15, -50, -49, -40, -41, -54, -50, 1.94, 4.65);

        Point point16 = new Point(16, -52, -49, -43, -40, -47, -52, 0, 5.58);
        Point point17 = new Point(17, -36, -42, -46, -40, -46, -47, 0.97, 5.58);
        Point point18 = new Point(18, -45, -49, -32, -41, -49, -49, 1.94, 5.58);

        Point point19 = new Point(19, -47, -47, -52, -37, -50, -47, 0, 7.44);
        Point point20 = new Point(20, -36, -44, -47, -35, -46, -52, 0.97, 7.44);
        Point point21 = new Point(21, -45, -43, -43, -45, -45, -51, 1.94, 7.44);

        Point point22 = new Point(22, -44, -43, -54, -50, -47, -55, 0, 8.37);
        Point point23 = new Point(23, -43, -46, -51, -42, -44, -53, 0.97, 8.37);
        Point point24 = new Point(24, -47, -54, -44, -41, -46, -54, 1.94, 8.37);

        Point point25 = new Point(25, -62, -60, -50, -44, -52, -60, 0, 9.30);
        Point point26 = new Point(26, -43, -49, -49, -45, -46, -61, 0.97, 9.30);
        Point point27 = new Point(27, -44, -43, -40, -53, -46, -52, 1.94, 9.30);

        Point point28 = new Point(28, -40, -46, -47, -46, -53, -55, 0, 10.23);
        Point point29 = new Point(29, -44, -46, -41, -44, -42, -47, 0.97, 10.23);
        Point point30 = new Point(30, -41, -50, -46, -49, -43, -53, 1.94, 10.23);

        Point point31 = new Point(31, -43, -56, -60, -50, -49, -59, 0, 11.16);
        Point point32 = new Point(32, -46, -45, -53, -46, -43, -56, 0.97, 11.16);
        Point point33 = new Point(33, -46, -45, -54, -54, -47, -60, 1.94, 11.16);

        Point point34 = new Point(34, -34, -55, -60, -60, -43, -57, 0, 12.09);
        Point point35 = new Point(35, -38, -54, -41, -51, -41, -58, 0.97, 12.09);
        Point point36 = new Point(36, -55, -58, -45, -51, -40, -51, 1.94, 12.09);

        ArrayList<Point> dataList = new ArrayList<>();
        dataList.add(point1);
        dataList.add(point2);
        dataList.add(point3);
        dataList.add(point4);
        dataList.add(point5);
        dataList.add(point6);
        dataList.add(point7);
        dataList.add(point8);
        dataList.add(point9);
        dataList.add(point10);
        dataList.add(point11);
        dataList.add(point12);
        dataList.add(point13);
        dataList.add(point14);
        dataList.add(point15);
        dataList.add(point16);
        dataList.add(point17);
        dataList.add(point18);
        dataList.add(point19);
        dataList.add(point20);
        dataList.add(point21);
        dataList.add(point22);
        dataList.add(point23);
        dataList.add(point24);
        dataList.add(point25);
        dataList.add(point26);
        dataList.add(point27);
        dataList.add(point28);
        dataList.add(point29);
        dataList.add(point30);
        dataList.add(point31);
        dataList.add(point32);
        dataList.add(point33);
        dataList.add(point34);
        dataList.add(point35);
        dataList.add(point36);

        return dataList;
    }
}
