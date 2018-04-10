package com.example.administrator.SmartParking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.SmartParking.database.MacInfo;
import com.example.administrator.SmartParking.database.SQLiteHelper;
import com.example.administrator.SmartParking.database.Wifi;
import com.example.administrator.SmartParking.database.testRecord;

public class ThirdActivity extends Activity {
    //Wi-Fi管理器
    private WifiManager mMgr = null;
    private FooWifiMonitor mMonitor = null;
    //定时器
    Timer timer = null;
    TimerTask task = null;
    //扫描次数
    private int times = 0;
    //指示符
    private int id = 1;
    private long exitTime = 0;
    private long clearTableTime = 0;
    private boolean continueFlag = false;
    private boolean readdFlag = false;
    private boolean addTestFlag = false;
    //数据收集
    private float alpha = (float) 0.75;
    private HashMap<String, Wifi> wifiMap = new HashMap<>();
    private MacInfo macInfo = new MacInfo();

    //界面数据
    private EditText etID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  //Activity 的生命周期
        setContentView(R.layout.third_activity);   //界面

        etID = (EditText) findViewById(R.id.etID);

        //初始化
        init();
    }

    private void init() {
        //获取Wi-Fi管理器
        final String sname = Context.WIFI_SERVICE;
        mMgr = (WifiManager) (this.getSystemService(sname));
        //
        if (!mMgr.isWifiEnabled()) {
            enableWifi();
        }
        //初始化监视器和意向过滤器
        // ActivityManagerService在收到SCAN_RESULTS_AVAILABLE_ACTION类型的广播时，就会分发给mMonitor实例的onReceive函数
        mMonitor = new FooWifiMonitor();
        final String aname = WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
        IntentFilter filter = new IntentFilter(aname);
        //注册监视器
        this.registerReceiver(mMonitor, filter);
    }

    private void enableWifi() { //启动Wi-Fi
        Intent i = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
        this.startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消监视器的注册
        this.unregisterReceiver(mMonitor);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                scanWifiInfo();
            }
            super.handleMessage(msg);
        }
    };

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
        }

        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    // 需要做的事:发送消息
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            };
        }

        if (timer != null && task != null)
            timer.schedule(task, 50, 2000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void print(String text) {
        ((EditText) findViewById(R.id.text)).append(text + "\n");
    }

    public void startScan(View view) {
        startTimer();
    }

    public void scanWifiInfo() {
        if (times == 20) {
            stopTimer();
            times = 1;
            if (continueFlag) {
                String _id = etID.getText().toString();
                SQLiteHelper helper = ((SmartParkingApplication)getApplication()).getSQLiteHelper();
                SQLiteDatabase database = helper.open();

                if (macInfo.macSet.size() == 0) {
                    macInfo.macSet = MacInfo.getmacSet(database);
                }
                update(database, _id);
                continueFlag = false;
            }
            else if (readdFlag) {
                String _id = etID.getText().toString();
                SQLiteHelper helper = ((SmartParkingApplication)getApplication()).getSQLiteHelper();
                SQLiteDatabase database = helper.open();

                if (macInfo.macSet.size() == 0) {
                    macInfo.macSet = MacInfo.getmacSet(database);
                }
                add(database, _id);
                readdFlag = false;
            }
            else if (addTestFlag) {
                SQLiteHelper helper = ((SmartParkingApplication)getApplication()).getSQLiteHelper();
                SQLiteDatabase database = helper.open();

                addTest(database);
                addTestFlag = false;
            }
            else {
                storeDB();
            }
        } else {
            mMgr.startScan();
            times++;
        }
    }

    private void addTest(SQLiteDatabase database) {
        int i = 1;
        HashSet<String> removeSet = new HashSet<>();
        ContentValues values = new ContentValues();
        values.put("SSID", "TEST");
        for (String mac : macInfo.macSet) {
            if (i > 6) {
                removeSet.add(mac);
            } else {
                Wifi wifi = wifiMap.get(mac);
                if (wifi != null) {
                    values.put("RSSI" + i, (int) wifi.rssi);
                } else {
                    values.put("RSSI" + i, -100);
                }
                i++;
            }
        }
        for (String mac : removeSet) {
            macInfo.macSet.remove(mac);
        }
        while (i <= 6) {
            values.put("RSSI" + i, -100);
            i++;
        }
        long result = database.insert("testRecord", null, values);
        if (result != -1) {
            Toast.makeText(getApplicationContext(), "新增Test数据", Toast.LENGTH_LONG).show();
        }
        database.close();
    }

    private void add(SQLiteDatabase database, String _id) {
        int i = 1;
        HashSet<String> removeSet = new HashSet<>();
        ContentValues values = new ContentValues();
        values.put("_id", _id);
        values.put("SSID", "TEST");
        for (String mac : macInfo.macSet) {
            if (i > 6) {
                removeSet.add(mac);
            } else {
                Wifi wifi = wifiMap.get(mac);
                if (wifi != null) {
                    values.put("RSSI" + i, (int) wifi.rssi);
                } else {
                    values.put("RSSI" + i, -100);
                }
                i++;
            }
        }
        for (String mac : removeSet) {
            macInfo.macSet.remove(mac);
        }
        while (i <= 6) {
            values.put("RSSI" + i, -100);
            i++;
        }
        long result = database.insert("wifi", null, values);
        if (result != -1) {
            id = Integer.parseInt(etID.getText().toString());
            Toast.makeText(getApplicationContext(), "更新成功ID"+_id, Toast.LENGTH_LONG).show();
        }
        database.close();
    }

    private void update(SQLiteDatabase database, String _id) {
        int i = 1;
        ArrayList<Integer> wifiList = Wifi.getWifiList(database, _id);
        HashSet<String> removeSet = new HashSet<>();

        ContentValues values = new ContentValues();
        values.put("SSID", "TEST");
        for (String mac : macInfo.macSet) {
            if (i > 6) {
                removeSet.add(mac);
            } else {
                Wifi wifi = wifiMap.get(mac);
                if (wifi != null) {
                    values.put("RSSI" + i, (int) ((alpha * wifi.rssi) + (1 - alpha) * wifiList.get(i)));
                } else {
                    values.put("RSSI" + i, -100);
                }
                i++;
            }
        }
        for (String mac : removeSet) {
            macInfo.macSet.remove(mac);
        }
        while (i <= 6) {
            values.put("RSSI" + i, -100);
            i++;
        }
        if (database.update("wifi", values, "_id=?", new String[]{String.valueOf(_id)}) > 0) {
            database.close();
            id = Integer.parseInt(etID.getText().toString());
            print("ID" + id);
            id++;
            etID.setText(String.valueOf(id));
            Toast.makeText(getApplicationContext(), "更新成功ID"+_id, Toast.LENGTH_LONG).show();
        }
    }

    private void storeDB() {
        macInfo.macSet = Wifi.addList((SmartParkingApplication)getApplication(), wifiMap, macInfo.macSet);
        print("ID" + id);
        id++;
        etID.setText(String.valueOf(id));
        if (macInfo.columCount == 0)
            if (macInfo.addList((SmartParkingApplication)getApplication(), macInfo.macSet))
                macInfo.columCount++;
    }

    //清空Map
    public void clearMapInfo(View view) {
        times = 1;
        wifiMap.clear();
        Toast.makeText(ThirdActivity.this, "wifiMap已清空", Toast.LENGTH_SHORT).show();
    }

    //清空表格
    public void clearTable(View view) {
        if ((System.currentTimeMillis() - clearTableTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次清空表",
                    Toast.LENGTH_SHORT).show();
            clearTableTime = System.currentTimeMillis();
        } else {
            SQLiteHelper helper = ((SmartParkingApplication)getApplication()).getSQLiteHelper();
            SQLiteDatabase database = helper.getWritableDatabase();
            Wifi.drop(database);
            MacInfo.drop(database);
            testRecord.drop(database);
            helper.onCreate(database);
            database.close();
            Toast.makeText(ThirdActivity.this, "table已清空", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteRecord(View view) {
        String _id = etID.getText().toString();
        SQLiteHelper helper = ((SmartParkingApplication) getApplication()).getSQLiteHelper();
        SQLiteDatabase database = helper.open();
        int result = database.delete("wifi", "_id = ?",
                new String[]{String.valueOf(_id)});
        database.close();
        if (result != 0) {
            Toast.makeText(this, "已删除ID=" + _id + "的数据", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "不存在ID=" + _id + "的数据", Toast.LENGTH_SHORT).show();
        }
    }

    public void continueRecord(View view) {
        continueFlag = true;
        startTimer();
    }

    public void getmacaddr(View view) {
        SQLiteHelper helper = ((SmartParkingApplication)getApplication()).getSQLiteHelper();
        SQLiteDatabase database = helper.getWritableDatabase();

        macInfo.macSet = MacInfo.getmacSet(database);
        if (macInfo.macSet.size() != 0) {
            Toast.makeText(this, "已获取macset", Toast.LENGTH_SHORT).show();
        }
    }

    public void readd(View view) {
        readdFlag = true;
        startTimer();
    }

    public void addTestRecord(View view) {
        addTestFlag = true;
        startTimer();
    }

    class FooWifiMonitor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(ThirdActivity.this, "扫描" + times + "次", Toast.LENGTH_SHORT).show();
            //获取扫描结果
            List<ScanResult> results = mMgr.getScanResults();
            if (results != null) {
                for (ScanResult result : results) {
                    if (result.SSID.contains("mTest")) {
                        if (wifiMap.keySet().contains(result.BSSID)) {
                            wifiMap.get(result.BSSID).rssi = alpha * result.level +
                                    (1-alpha) * wifiMap.get(result.BSSID).rssi;
                        } else {
                            Wifi wifi = new Wifi();
                            wifi.SSID = result.SSID;
                            wifi.MAC = result.BSSID;
                            wifi.rssi = result.level;
                            wifiMap.put(wifi.MAC, wifi);
                        }
                    }
                }
//                for (int j = 0; j < results.size(); ++j) {
//                    if (results.get(j).SSID.contains("319"))
//                        print("==== 搜索信息#" + (j + 1) + "：" + getScanResult(results.get(j)));
//                }
            }
        }


//        private String getScanResult(ScanResult scanResult) {
//            StringBuffer sb = new StringBuffer("\nSSID：" + scanResult.SSID);
//            sb.append("\nMAC：" + scanResult.BSSID);
//            sb.append("\n性能" + scanResult.capabilities);
//            sb.append("\n频率" + scanResult.frequency + " MHz");
//            sb.append("\n信号等级" + scanResult.level + " dBm");
//            return (sb.toString());
//        }
    }

}




