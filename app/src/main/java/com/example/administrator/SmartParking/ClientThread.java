package com.example.administrator.SmartParking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

//import android.annotation.SuppressLint;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


//@SuppressLint("HandlerLeak")
public class ClientThread extends Thread {
	private OutputStream outputStream = null;
	private InputStream inputStream = null;
	private Socket socket;
	private SocketAddress socketAddress;
	public static Handler childHandler;
	private boolean RxFlag = true;
	private RxThread rxThread;
	//final int TEXT_INFO = 12;
	static final int RX_EXIT = 11;
	static final int TX_DATA = 10;
	Context mainContext;
	Message msg;
	private String strIP;
	final int SERVER_PORT = 33333;//和电脑创建的服务器配对
	byte cNodeData[][]= new byte[4][2];//[4] 0=车位一 1=车位二 2=车位三 3=车位四

	public ClientThread(String ip) {
		strIP = ip;
	}	

	//连接网络
	void connect() {
		RxFlag = true;
		socketAddress = new InetSocketAddress(strIP, SERVER_PORT);
		socket = new Socket();

		try {
			socket.connect(socketAddress, SERVER_PORT);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();

			msg = MainActivity.mainHandler.obtainMessage(MainActivity.TIPS_UPDATE_UI, "连接成功");
			MainActivity.mainHandler.sendMessage(msg);

			rxThread = new RxThread();
			rxThread.start();

		} catch (IOException e) {	
			try {
				sleep(10);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			msg = MainActivity.mainHandler.obtainMessage(MainActivity.TIPS_UPDATE_UI, "无法连接到服务器");
			MainActivity.mainHandler.sendMessage(msg);
			e.printStackTrace();
		} catch (NumberFormatException e) {

		}
	}

	@SuppressLint("HandlerLeak")
	void initChildHandler() {
		
		Looper.prepare();  //在子线程中创建Handler必须初始化Looper

		childHandler = new Handler() {
			//子线程消息处理中心
			public void handleMessage(Message msg) {

				//接收主线程及其他线程的消息并处理...
				switch (msg.what) {
				case TX_DATA:
					int len = msg.arg1;				

					try {
						outputStream.write((byte [])msg.obj, 0, len);
						outputStream.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;

				case RX_EXIT:
					RxFlag = false;
					try {
						if (socket.isConnected()) {
							inputStream.close();
							outputStream.close();
							socket.close();
						}
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					childHandler.getLooper().quit();// 结束消息队列

					break;

				default:
					break;
				}

			}
		};

		// 启动该线程的消息队列
		Looper.loop();

	}

	public void run() {
		connect();
		initChildHandler();
		msg = MainActivity.mainHandler.obtainMessage(MainActivity.TIPS_UPDATE_UI, "与服务器断开连接");
		MainActivity.mainHandler.sendMessage(msg);
	}

	//socket 接收线程（这块发送的字节类型还需要修改）
	public class RxThread extends Thread {
		public void run() {
			try {
				while (socket.isConnected() && RxFlag) {
					byte RxBuf[] = new byte[256];
					int len = inputStream.read(RxBuf);

					if (len > 4 && RxBuf[0] == 0x3A) {
						int index = 4, i;

						// 解析数据
						switch (RxBuf[3]) {
						case 0x01:
							for (i = 0; i <  MainActivity.MAX_NODE; i++) {
								System.arraycopy(RxBuf, index, MainActivity.iNodeData[i], 0, 2);
								//将RxBuf的索引index	开始的4个数据复制到目标的索引为0的位置上
								index += 4;
							}
							
							msg = MainActivity.mainHandler.obtainMessage(MainActivity.RX_DATA_UPDATE_UI,"Connect");
							MainActivity.mainHandler.sendMessage(msg);
							break;
						case 0x0A:  //循环在刷新画面，写操作时这里可以不用刷新

							break;

						default:
							break;
						}
					}else if (len < 0){
						msg = MainActivity.mainHandler.obtainMessage(MainActivity.TIPS_UPDATE_UI,
								"与服务器断开连接");
						MainActivity.mainHandler.sendMessage(msg);

						//退出接收线程
						msg = childHandler.obtainMessage(RX_EXIT);
						childHandler.sendMessage(msg);
						break;

					}

					//sleep(100);
				}
				
				if (socket.isConnected())
					socket.close();
				
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}