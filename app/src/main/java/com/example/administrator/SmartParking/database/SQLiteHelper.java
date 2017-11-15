package com.example.administrator.SmartParking.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.crypto.Mac;

/**
 * SQLiteHelper类
 * 用于管理SQLite
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WifiInfo.db";   // 数据库名字
    private static final int DATABASE_VERSION = 1;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Wifi.create(sqLiteDatabase);
        MacInfo.create(sqLiteDatabase);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.execSQL("pragma foreign_keys=1;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Wifi.drop(sqLiteDatabase);
        MacInfo.drop(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    public SQLiteDatabase open() { return getWritableDatabase(); }

    public void create() { open(); }
}
