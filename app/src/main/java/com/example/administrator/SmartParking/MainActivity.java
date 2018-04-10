package com.example.administrator.SmartParking;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private TextView textNode1, textNode2, textNode3, textNode4, textStatus1,
            textStatus2, textStatus3, textStatus4;
    TextView textTips;
    private ImageView ivCar1, ivCar2, ivCar3, ivCar4;
    public static Handler mainHandler;
    private ClientThread clientThread = null;
    private Timer mainTimer;
    static final int RX_DATA_UPDATE_UI = 1;
    static final int TX_DATA_UPDATE_UI = 2;
    static final int TIPS_UPDATE_UI = 3;
    static final int READ_ALL_INFO = 4;
    //final int WRITE_LAMP = 5;
    //final int WRITE_LAMP_ALL = 6;

    static final int MAX_NODE = 4;

    static byte iNodeData[][]= new byte[MAX_NODE][2];//[MAX_NODE] 0=车位一 1=车位二 2=车位三 3=车位四
    byte SendBuf[] = { 0x3A, 0x00, 0x01, 0x0A, 0x00, 0x00, 0x23, 0x00 };
    private Message MainMsg;
    //记录用户首次点击返回键的时间
    private long firstTime = 0;



   //按钮导航
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (clientThread == null&&item.getItemId()==R.id.navigation_notifications) {
                textTips.setText("提示信息：请先连接网络");
                ivCar1.setImageResource(R.drawable.car1);
                return true;
             }
             else{
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    showDialog(MainActivity.this);
                    return true;
                case R.id.navigation_dashboard:
                    Intent intent=new Intent(MainActivity.this,ThirdActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_notifications:
//                    Intent intent_new=new Intent(MainActivity.this,ThirdActivity.class);
//                    startActivity(intent_new);
                    if (clientThread != null) {
                        MainMsg = mainHandler.obtainMessage(TX_DATA_UPDATE_UI,
                                READ_ALL_INFO, 0xFF);
                        mainHandler.sendMessage(MainMsg);
                    } else {
                        MainMsg = MainActivity.mainHandler.obtainMessage(MainActivity.TIPS_UPDATE_UI,
                                "与服务器断开连接");
                        MainActivity.mainHandler.sendMessage(MainMsg);
                    }
                    return true;
            }}
            return false;
        }

    };

    public MainActivity() {
    }
    //入口
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast toast = Toast.makeText(getApplicationContext(),"正在启动...", Toast.LENGTH_SHORT);
        toast.show();
        initControl();
        initMainHandler();

    }


   private void showDialog(Context context) {
        final EditText editIP = new EditText(context);
        editIP.setText(R.string.Default_IP);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("请输入服务器IP，默认端口33333");
        builder.setView(editIP);
        builder.setPositiveButton("连接", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String strIpAddr = editIP.getText().toString();
                boolean ret = isIPAddress(strIpAddr);

                if (ret) {
                    textTips.setText(String.format("%s%s", getString(R.string.IP_Tag), strIpAddr));
                } else {
                    textTips.setText(R.string.IPAddressError);
                    return;
                }

                clientThread = new ClientThread(strIpAddr);//建立客户端线程
                clientThread.start();


                mainTimer = new Timer();//定时查询所有终端信息
                setTimerTask();
            }
        });
        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (clientThread != null) {
                    MainMsg = ClientThread.childHandler
                            .obtainMessage(ClientThread.RX_EXIT);
                    ClientThread.childHandler.sendMessage(MainMsg);
                    textTips.setText("与服务器断开连接");
                }
                mainTimer.cancel();
                mainTimer = null;
            }
        });

        builder.show();
    }

    private void setTimerTask() {
        mainTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (clientThread != null) {
                    MainMsg = mainHandler.obtainMessage(TX_DATA_UPDATE_UI,
                            READ_ALL_INFO, 0xFF);
                    mainHandler.sendMessage(MainMsg);
                }
            }
        }, 500, 1000);//表示500毫秒之后，每隔1000毫秒执行一次
    }

    //通知客户端线程 发送消息
    void SendData(byte buffer[], int len) {
        MainMsg = ClientThread.childHandler.obtainMessage(ClientThread.TX_DATA,
                len, 0, (Object) buffer);
        ClientThread.childHandler.sendMessage(MainMsg);
    }

    @SuppressLint("HandlerLeak")
    void initMainHandler() {
        mainHandler = new Handler() {

            //主线程消息处理中心
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RX_DATA_UPDATE_UI:
                        if (iNodeData[0][0] == 1){
                            textStatus1.setText(R.string.car_status_yes);
                            ivCar1.setImageResource(R.drawable.car);}//显示有车
                        else{
                            textStatus1.setText(R.string.car_status_no);
                            ivCar1.setImageResource(R.drawable.car1);}//显示无车


                        //终端2
                        if (iNodeData[1][0] == 1){
                            textStatus2.setText(R.string.car_status_yes);
                            ivCar2.setImageResource(R.drawable.car);}//显示有车
                        else{
                            textStatus2.setText(R.string.car_status_no);
                            ivCar2.setImageResource(R.drawable.car1);}//显示无车

                        //终端3
                        if (iNodeData[2][0] == 1){
                            textStatus3.setText(R.string.car_status_yes);
                            ivCar3.setImageResource(R.drawable.car);}
                        //显示有车

                        else{
                            textStatus3.setText(R.string.car_status_no);
                            ivCar3.setImageResource(R.drawable.car1);}//显示无车

                        //终端4
                        if (iNodeData[3][0] == 1){
                            textStatus4.setText(R.string.car_status_yes);
                            ivCar4.setImageResource(R.drawable.car);//显示有车
                        }
                        else{
                            textStatus4.setText(R.string.car_status_no);
                            ivCar4.setImageResource(R.drawable.car1);//显示无车
                        }

                    case TX_DATA_UPDATE_UI: //msg.arg1保存功能码 arg2保存终端地址
                        switch (msg.arg1) {
                            case READ_ALL_INFO:
                                SendBuf[2] = (byte) msg.arg2;//0xFF;
                                SendBuf[3] = 0x01;           //FC
                                SendBuf[4] = (byte) 0xC4;
                                SendBuf[5] = (byte) 0x23;

                                SendData(SendBuf, 6); //查询所有终端报文3A 00 FF 01 C4 23
                                break;

                            default:
                                break;
                        }
                        break;
                    case TIPS_UPDATE_UI:
                        String str = (String) msg.obj;
                        textTips.setText(str);
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    void initControl() {
        // 底部导航
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        //----------------------node 1----------------------
        textNode1 = (TextView) findViewById(R.id.node_title1);
        textNode1.setBackgroundResource(R.drawable.carsite);
        textStatus1 = (TextView) findViewById(R.id.carStatus1);
        textStatus1.setText(R.string.car_status_yes);
        ivCar1 = (ImageView) findViewById(R.id.image_car1);
        ivCar1.setImageResource(R.drawable.car);

        //----------------------node 2----------------------
        textNode2 = (TextView) findViewById(R.id.node_title2);
        textNode2.setBackgroundResource(R.drawable.carsite2);
        textStatus2 = (TextView) findViewById(R.id.carStatus2);
        textStatus2.setText(R.string.car_status_yes);
        ivCar2 = (ImageView) findViewById(R.id.image_car2);
        ivCar2.setImageResource(R.drawable.car);


        //----------------------node 3----------------------
        textNode3 = (TextView) findViewById(R.id.node_title3);
        textNode3.setBackgroundResource(R.drawable.carsite3);
        textStatus3 = (TextView) findViewById(R.id.carStatus3);
        textStatus3.setText(R.string.car_status_yes);
        ivCar3 = (ImageView) findViewById(R.id.image_car3);
        ivCar3.setImageResource(R.drawable.car);


        //----------------------node 4----------------------
        textNode4 = (TextView) findViewById(R.id.node_title4);
        textNode4.setBackgroundResource(R.drawable.carsite4);
        textStatus4 = (TextView) findViewById(R.id.carStatus4);
        textStatus4.setText(R.string.car_status_yes);
        ivCar4 = (ImageView) findViewById(R.id.image_car4);
        ivCar4.setImageResource(R.drawable.car);

        textTips = (TextView) findViewById(R.id.Tips);
        textTips.setText(R.string.init_tips);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainTimer.cancel();
    }

    /**
     * 响应onKeyDown事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //判断输入IP是否合法
    private boolean isIPAddress(String ipaddr) {
        boolean flag = false;
        Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
        Matcher m = pattern.matcher(ipaddr);
        flag = m.matches();
        return flag;
    }

    private byte XorCheckSum(byte[] pBuf, int len) {
        int i;
        byte byRet = 0;
        if (len == 0)
            return byRet;
        else
            byRet = pBuf[0];
        for (i = 1; i < len; i++)
            byRet = (byte) (byRet ^ pBuf[i]);
        return byRet;
    }

}

