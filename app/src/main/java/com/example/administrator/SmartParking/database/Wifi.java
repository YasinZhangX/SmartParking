package com.example.administrator.SmartParking.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.administrator.SmartParking.SmartParkingApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Wifi Instance
 */

public class Wifi {
    public int _id;
    public String SSID;
    public String MAC;
    public float rssi;

    static void create(SQLiteDatabase database) {
        String createTable = "create table if not exists wifi ("
                + "_id integer primary key autoincrement, "
                + "SSID text not null, "
                + "MAC text not null"
                + "RSSI int not null );";
        database.execSQL(createTable);
    }

    static void drop(SQLiteDatabase database)
    {
        String dropTable = "drop table if exists wifi";
        database.execSQL(dropTable);
    }

    public static boolean addList(SmartParkingApplication application, HashMap<String, Wifi> wifiHashMap,
                               Set<String> macSet) {
        SQLiteHelper helper = application.getSQLiteHelper();
        SQLiteDatabase database = helper.getWritableDatabase();

        try {
            if (macSet == null) {
                macSet = wifiHashMap.keySet();
            }
            database.beginTransaction(); //开始事务
            for (String mac : macSet) {
                Wifi wifi = wifiHashMap.get(mac);
                if (wifi != null) {
                    ContentValues values = new ContentValues();//是用map封装的对象，用来存放值
                    values.put("SSID", wifi.SSID);
                    values.put("MAC", wifi.MAC);
                    values.put("RSSI", (int) wifi.rssi);
                    //table: 表名 , nullColumnHack：可以为空，标示添加一个空行, values:数据一行的值 , 返回值：代表添加这个新行的Id ，-1代表添加失败
                    long result = database.insert("wifi", null, values);
                    if (result == -1)
                        return false;
                } else {
                    ContentValues values = new ContentValues();//是用map封装的对象，用来存放值
                    values.put("SSID", "SCUNET");
                    values.put("MAC", mac);
                    values.put("RSSI", -100);
                    long result = database.insert("wifi", null, values);
                    if (result == -1)
                        return false;
                }
            }
            database.setTransactionSuccessful();
            Toast.makeText(application.getApplicationContext(), "新增成功", Toast.LENGTH_LONG).show();

        } finally {
            database.endTransaction(); //结束事务
            database.close();
            return true;
        }
    }
}
