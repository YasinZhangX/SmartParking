package com.example.administrator.SmartParking.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * testRecord table
 */

public class testRecord {
    public int _id;
    public String SSID;
    public String MAC;
    public float rssi;

    public static void create(SQLiteDatabase database) {
        String createTable = "create table if not exists testRecord ("
                + "_id integer primary key autoincrement, "
                + "SSID text not null, "
                + "RSSI1 int not null, "
                + "RSSI2 int not null, "
                + "RSSI3 int not null, "
                + "RSSI4 int not null, "
                + "RSSI5 int not null, "
                + "RSSI6 int not null );";
        database.execSQL(createTable);
    }

    public static void drop(SQLiteDatabase database)
    {
        String dropTable = "drop table if exists testRecord";
        database.execSQL(dropTable);
    }
}
