package com.example.administrator.SmartParking.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.example.administrator.SmartParking.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.Mac;

/**
 * SQLiteHelper类
 * 用于管理SQLite
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private final int BUFFER_SIZE = 400000;
    private static final String DATABASE_NAME = "wifiinfo.db";   // 数据库名字
    public static final String PACKAGE_NAME = "com.example.administrator.SmartParking"; //包名
    public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() +
            "/" + PACKAGE_NAME + "/databases";  //数据库的绝对路径( /data/data/com.*.*(package name)/databases )
    private static final int DATABASE_VERSION = 1;

    private Context context;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Wifi.create(sqLiteDatabase);
        MacInfo.create(sqLiteDatabase);
        testRecord.create(sqLiteDatabase);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.execSQL("pragma foreign_keys=1;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Wifi.drop(sqLiteDatabase);
        MacInfo.drop(sqLiteDatabase);
        testRecord.drop(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    public SQLiteDatabase open() {
        String filePath = DB_PATH + "/" + DATABASE_NAME;
        try {
            File file = new File(filePath);
            if (!file.exists()) { // 判断文件是否存在，通过输入输出流把数据库拷贝到filePath下
                InputStream inputStream = context.getResources().openRawResource(R.raw.wifiinfo);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[BUFFER_SIZE];
                int readCount;
                while ((readCount = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, readCount);
                }
                fileOutputStream.close();
                inputStream.close();
            }
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(filePath, null);
            return database;
        } catch (FileNotFoundException e) {
            Log.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Database", "IO exception");
            e.printStackTrace();
        }
        return null;
    }

    public void create() { open(); }
}
