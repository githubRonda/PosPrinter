package com.ronda.posprinter.engine.serialport;

import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/02/27
 * Version: v1.0
 */

public class SerialPortService {


    private SerialPort mSerialPort;
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    private ReadThread mReadThread;

    private boolean isActive;


   // 无需更新UI，所以不需要 Handler 参数
    public SerialPortService(String devicePath, int baudrate) {

       // mHandler = handler;

        try {
            // mSerialPort = new SerialPort(new File("/dev/ttyS1"), 9600, 0);
            mSerialPort = new SerialPort(new File(devicePath), baudrate, 0); // 创建 SerialPort

            // 获取输入输出流，这样就可以对串口进行数据的读写了
            mInputStream = mSerialPort.getInputStream();
            mOutputStream = mSerialPort.getOutputStream();

            mReadThread = new ReadThread();
            mReadThread.start();

            isActive = true;
        } catch (IOException e) {
            e.printStackTrace();

            isActive = false;
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void write(byte[] data) {
        try {
            mOutputStream.write(data);

            Log.w("TAG", "write data: " + new String(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSerial() {
        isActive = false;

//        mHandler.removeCallbacksAndMessages(null);
//        mHandler = null;

        if (mReadThread != null) {
            mReadThread.interrupt();
            mReadThread = null;
        }

        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }

        try {
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
            if (mOutputStream != null) {
                mOutputStream.close();
                mOutputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 不需要读取数据
     */
    class ReadThread extends Thread {

        private StringBuilder sb = new StringBuilder();
        private byte[] buf = new byte[128];

        @Override
        public void run() {

//            while (isActive) {
//                try {
//                    int len = mInputStream.read(buf);
//                    //Log.i("TAG", "length is:" + len + ",data is:" + new String(buf, 0, len));
//                    if (len != -1 && mHandler != null) {
//
//                        sb.append(new String(buf, 0, len));
//                        Log.i("TAG", sb.toString());
//
//                        mHandler.obtainMessage(0, new String(buf, 0, len)).sendToTarget();
//                    }
//                    Thread.sleep(200);  // 间隔200毫秒
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }
}
