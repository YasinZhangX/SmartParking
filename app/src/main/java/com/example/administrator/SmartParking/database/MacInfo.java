package com.example.administrator.SmartParking.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.SmartParking.SmartParkingApplication;

import java.util.HashSet;
import java.util.Set;

/**
 * Mac Instance
 */

public class MacInfo {

    public Set<String> macSet = new HashSet<>();
    public int columCount = 0;

    public static void create(SQLiteDatabase database) {
        String createTable = "create table if not exists macInfo ("
                + "_id integer primary key autoincrement, "
                + "MAC1 text not null, "
                + "MAC2 text not null, "
                + "MAC3 text not null, "
                + "MAC4 text not null, "
                + "MAC5 text not null, "
                + "MAC6 text not null);";
        database.execSQL(createTable);
    }

    public static void drop(SQLiteDatabase database)
    {
        String dropTable = "drop table if exists macInfo";
        database.execSQL(dropTable);
    }

    public boolean addList(SmartParkingApplication application, Set<String> macSet)
    {
        SQLiteHelper helper = application.getSQLiteHelper();
        SQLiteDatabase database = helper.getWritableDatabase();

        int i = 1;
        ContentValues values = new ContentValues();//是用map封装的对象，用来存放值
        for (String mac : macSet) {
            values.put("MAC"+ i, mac);
            i++;
        }
        while (i <= 6) {
            values.put("MAC"+ i, "00:00:00:00:00:00");
            i++;
        }
        long result = database.insert("macInfo", null, values);
        database.close();
        if (result == -1)
            return false;
        else
            return true;
    }
}
