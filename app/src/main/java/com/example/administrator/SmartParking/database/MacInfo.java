package com.example.administrator.SmartParking.database;

import android.content.ContentValues;
import android.database.Cursor;
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

    public static Set<String> getmacSet(SQLiteDatabase database) {
        Set<String> macSet = new HashSet<>();
        String[] projection = {"_id", "MAC1", "MAC2", "MAC3", "MAC4", "MAC5", "MAC6"};
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = "_id ASC";
        Cursor c = database.query("macInfo", projection, selection, selectionArgs,
                null, null, sortOrder);
        c.moveToFirst();
        if (!c.isAfterLast()) {
            for (int i = 1; i <= 6; i++) {
                String mac = c.getString(c.getColumnIndex("MAC"+i));
                if (mac.hashCode() != "00:00:00:00:00:00".hashCode())
                    macSet.add(mac);
            }
            c.moveToNext();
        }
        c.close();
        return macSet;
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
        return (result != -1);
    }
}
