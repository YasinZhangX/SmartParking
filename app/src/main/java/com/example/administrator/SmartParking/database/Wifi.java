package com.example.administrator.SmartParking.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.administrator.SmartParking.SmartParkingApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Wifi Instance
 */

public class Wifi {
    public int _id;
    public String SSID;
    public String MAC;
    public float rssi;

    public static void create(SQLiteDatabase database) {
        String createTable = "create table if not exists wifi ("
                + "_id integer primary key autoincrement, "
                + "SSID text not null, "
                + "RSSI1 int not null, "
                + "RSSI2 int not null, "
                + "RSSI3 int not null, "
                + "RSSI4 int not null, "
                + "RSSI5 int not null, "
                + "RSSI6 int not null, "
                + "RSSI7 int not null, "
                + "RSSI8 int not null, "
                + "RSSI9 int not null, "
                + "RSSI10 int not null, "
                + "RSSI11 int not null, "
                + "RSSI12 int not null, "
                + "RSSI13 int not null, "
                + "RSSI14 int not null, "
                + "RSSI15 int not null, "
                + "RSSI16 int not null );";
        database.execSQL(createTable);
    }

    public static void drop(SQLiteDatabase database)
    {
        String dropTable = "drop table if exists wifi";
        database.execSQL(dropTable);
    }

    public static Set<String> addList(SmartParkingApplication application, HashMap<String, Wifi> wifiHashMap,
                               Set<String> macSet) {
        int i = 1;
        SQLiteHelper helper = application.getSQLiteHelper();
        SQLiteDatabase database = helper.getWritableDatabase();
        HashSet<String> removeSet = new HashSet<>();

        if (macSet.size() == 0) {
            macSet = wifiHashMap.keySet();
        }
        database.beginTransaction(); //开始事务
        ContentValues values = new ContentValues();//是用map封装的对象，用来存放值
        values.put("SSID", "SCUNET");
        for (String mac : macSet) {
            if (i > 16) {
                removeSet.add(mac);
            } else {
                Wifi wifi = wifiHashMap.get(mac);
                if (wifi != null) {
                    values.put("RSSI" + i, (int) wifi.rssi);
                } else {
                    values.put("RSSI" + i, -100);
                }
            }
            i++;
        }
        for (String mac : removeSet) {
            macSet.remove(mac);
        }
        while (i <= 16) {
            values.put("RSSI" + i, -100);
            i++;
        }
        //table: 表名 , nullColumnHack：可以为空，标示添加一个空行, values:数据一行的值 , 返回值：代表添加这个新行的Id ，-1代表添加失败
        long result = database.insert("wifi", null, values);
        if (result == -1)
            return null;
        database.setTransactionSuccessful();
        Toast.makeText(application.getApplicationContext(), "新增成功", Toast.LENGTH_LONG).show();

        database.endTransaction(); //结束事务
        database.close();
        return macSet;
    }
}
