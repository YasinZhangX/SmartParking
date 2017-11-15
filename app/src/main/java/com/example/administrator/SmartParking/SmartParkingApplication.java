package com.example.administrator.SmartParking;

import android.app.Application;

import com.example.administrator.SmartParking.database.SQLiteHelper;

public class SmartParkingApplication extends Application {

    private SQLiteHelper helper;

    public SQLiteHelper getSQLiteHelper() {
        if (helper == null) {
            helper = new SQLiteHelper(this);
        }
        return helper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getSQLiteHelper().create();
    }
}
