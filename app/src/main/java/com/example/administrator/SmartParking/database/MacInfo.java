package com.example.administrator.SmartParking.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.SmartParking.SmartParkingApplication;

import java.util.HashSet;

/**
 * Mac Instance
 */

public class MacInfo {

    public HashSet<String> macSet = new HashSet<>();
    public int columCount = 0;

    static void create(SQLiteDatabase database) {
        String createTable = "create table if not exists macInfo ("
                + "_id integer primary key autoincrement, "
                + "MAC1 text not null, "
                + "MAC2 text not null"
                + "MAC3 text not null"
                + "MAC4 text not null"
                + "MAC5 text not null"
                + "MAC6 text not null"
                + "MAC7 text not null"
                + "MAC8 text not null"
                + "MAC9 text not null"
                + "MAC10 text not null"
                + "MAC11 text not null"
                + "MAC12 text not null"
                + "MAC13 text not null"
                + "MAC14 text not null"
                + "MAC15 text not null"
                + "MAC16 text not null);";
        database.execSQL(createTable);
    }

    static void drop(SQLiteDatabase database)
    {
        String dropTable = "drop table if exists wifi";
        database.execSQL(dropTable);
    }

    public boolean addList(SmartParkingApplication application, HashSet<String> macSet)
    {
        SQLiteHelper helper = application.getSQLiteHelper();
        SQLiteDatabase database = helper.getWritableDatabase();

        int i = 1;
        ContentValues values = new ContentValues();//是用map封装的对象，用来存放值
        for (String mac : macSet) {
            values.put("MAC"+ i, mac);
            i++;
        }
        long result = database.insert("wifi", null, values);
        database.close();
        if (result == -1)
            return false;
        else
            columCount++;
            return true;
    }
}
