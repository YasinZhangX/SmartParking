package com.example.administrator.SmartParking;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.SmartParking.KNN.KNN;
import com.example.administrator.SmartParking.KNN.Point;
import com.example.administrator.SmartParking.KNN.location;
import com.example.administrator.SmartParking.database.MacInfo;
import com.example.administrator.SmartParking.database.SQLiteHelper;
import com.example.administrator.SmartParking.database.Wifi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SecondActivity extends AppCompatActivity {
    //Wi-Fi管理器
    private WifiManager mMgr = null;
    private FooWifiMonitor mMonitor = null;
    //定时器
    Timer timer = null;
    TimerTask task = null;
    //扫描次数
    private int times = 0;
    //指示符
    private long exitTime = 0;
    //数据收集
    private float alpha = (float) 0.75;
    private HashMap<String, Wifi> wifiMap = new HashMap<>();
    private MacInfo macInfo = new MacInfo();
    Point x = new Point(0, 0, 0, 0, 0, 0, 0, 0, 0);
    location loc = null;

    private TextView mTextMessage;
    MainActivity s = new MainActivity();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:{
                    mTextMessage.setText("hello");
                    return true;}
                case R.id.navigation_dashboard:{
                    mTextMessage.setText("hello!");
                    return true;}
                case R.id.navigation_notifications:{
                    mTextMessage.setText("hello!!");
                    startScanLocation();
                    return true;}
            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_main);

        initControl();

    }

    void initControl() {
        mTextMessage = (TextView) findViewById(R.id.newword);
        mTextMessage.setText("导航服务");
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        init();
        getmacaddr();
    }

    /*     图像显示函数       */
    void show(location loc) {

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
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
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

        if (timer != null)
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

    public void startScanLocation() {
        startTimer();
    }

    public void scanWifiInfo() {
        if (times == 5) {
            stopTimer();
            times = 1;
            if (macInfo.macSet.size() == 0) {
                getmacaddr();
            }
            x = GeneratePoint();
            loc = KNN.KNN_GetLocation(x);
            show(loc);
        } else {
            mMgr.startScan();
            times++;
        }
    }

    private Point GeneratePoint() {
        int i = 1;
        HashSet<String> removeSet = new HashSet<>();
        Point z = new Point(0, 0, 0, 0, 0, 0, 0, 0, 0);
        for (String mac : macInfo.macSet) {
            if (i > 6) {
                removeSet.add(mac);
            } else {
                Wifi wifi = wifiMap.get(mac);
                if (wifi != null) {
                    z.setRx(i, (int) wifi.rssi);
                } else {
                    z.setRx(i, -100);
                }
                i++;
            }
        }
        for (String mac : removeSet) {
            macInfo.macSet.remove(mac);
        }
        while (i <= 6) {
            z.setRx(i, -100);
            i++;
        }

        return z;
    }

    public void getmacaddr() {
        SQLiteHelper helper = ((SmartParkingApplication)getApplication()).getSQLiteHelper();
        SQLiteDatabase database = helper.getWritableDatabase();

        macInfo.macSet = MacInfo.getmacSet(database);
        if (macInfo.macSet.size() != 0) {
            Toast.makeText(this, "已获取macset", Toast.LENGTH_SHORT).show();
        }
    }

    class FooWifiMonitor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(SecondActivity.this, "扫描" + times + "次", Toast.LENGTH_SHORT).show();
            //获取扫描结果
            List<ScanResult> results = mMgr.getScanResults();
            if (results != null) {
                for (ScanResult result : results) {
                    if (result.SSID.contains("mTest")) {
                        if (wifiMap.keySet().contains(result.BSSID)) {
                            wifiMap.get(result.BSSID).rssi = alpha * result.level +
                                    (1 - alpha) * wifiMap.get(result.BSSID).rssi;
                        } else {
                            Wifi wifi = new Wifi();
                            wifi.SSID = result.SSID;
                            wifi.MAC = result.BSSID;
                            wifi.rssi = result.level;
                            wifiMap.put(wifi.MAC, wifi);
                        }
                    }
                }
            }
        }
    }
}

