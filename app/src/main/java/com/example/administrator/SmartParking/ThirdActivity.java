package com.example.administrator.SmartParking;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.SmartParking.database.MacInfo;
import com.example.administrator.SmartParking.database.SQLiteHelper;
import com.example.administrator.SmartParking.database.Wifi;

public class ThirdActivity extends Activity {
    //Wi-Fi管理器
    private WifiManager mMgr = null;
    private FooWifiMonitor mMonitor = null;
    //定时器
    Timer timer = null;
    TimerTask task = null;
    //扫描次数
    private int times = 0;
    private float alpha = (float) 0.75;
    private HashMap<String, Wifi> wifiMap = new HashMap<>();
    private MacInfo macInfo = new MacInfo();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  //Activity 的生命周期
        setContentView(R.layout.third_activity);   //界面
        Intent intent = getIntent();
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

    class FooWifiMonitor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(ThirdActivity.this, "扫描" + times + "次", Toast.LENGTH_SHORT).show();
            //获取扫描结果
            List<ScanResult> results = mMgr.getScanResults();
            if (results != null) {
                for (ScanResult result : results) {
                    //if (result.SSID.hashCode() == "SCUNET".hashCode()) {
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
                    //}
                }
//                for (int j = 0; j < results.size(); ++j) {
//                    if (results.get(j).SSID.contains("319"))
//                        print("==== 搜索信息#" + (j + 1) + "：" + getScanResult(results.get(j)));
//                }
            }
        }


        private String getScanResult(ScanResult scanResult) {
            StringBuffer sb = new StringBuffer("\nSSID：" + scanResult.SSID);
            sb.append("\nMAC：" + scanResult.BSSID);
            sb.append("\n性能" + scanResult.capabilities);
            sb.append("\n频率" + scanResult.frequency + " MHz");
            sb.append("\n信号等级" + scanResult.level + " dBm");
            return (sb.toString());
        }
    }

    private void print(String text) {
        ((EditText) findViewById(R.id.text)).append(text + "\n");
    }

    public void startScan(View view) {
        startTimer();
    }

    public void scanWifiInfo() {
        if (times == 5) {
            stopTimer();
            times = 1;
            macInfo.macSet = Wifi.addList((SmartParkingApplication)getApplication(), wifiMap, macInfo.macSet);
            if (macInfo.columCount == 0)
                if (macInfo.addList((SmartParkingApplication)getApplication(), macInfo.macSet))
                    macInfo.columCount++;
        } else {
            mMgr.startScan();
            times++;
        }
    }

    //清空Map
    public void clearMapInfo(View view) {
        times = 1;
        wifiMap.clear();
        Toast.makeText(ThirdActivity.this, "wifiMap已清空", Toast.LENGTH_SHORT).show();
    }

    //清空表格
    public void clearTable(View view) {
        SQLiteHelper helper = ((SmartParkingApplication)getApplication()).getSQLiteHelper();
        SQLiteDatabase database = helper.getWritableDatabase();
        Wifi.drop(database);
        MacInfo.drop(database);
        helper.onCreate(database);
        database.close();
        Toast.makeText(ThirdActivity.this, "table已清空", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                scanWifiInfo();
            }
            super.handleMessage(msg);
        };
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

        if(timer != null && task != null )
            timer.schedule(task, 500, 3000);
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

}




