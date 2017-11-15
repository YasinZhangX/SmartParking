package com.example.administrator.SmartParking.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Wifi Instance
 */

public class Wifi {
    public int _id;
    public String SSID;
    public String MAC;
    public int level;

    public static void create(SQLiteDatabase database) {
        String createTable = "create table if not exists wifi ("
                + "_id integer primary key autoincrement, "
                + "SSID text not null, "
                + "MAC text not null"
                + "RSSI int not null );";
        database.execSQL(createTable);
    }

    public static void drop(SQLiteDatabase database)
    {
        String dropTable = "drop table if exists wifi";
        database.execSQL(dropTable);
    }
}
