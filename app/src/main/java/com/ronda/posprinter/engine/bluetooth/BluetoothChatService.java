package com.ronda.posprinter.engine.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ronda.posprinter.base.AppConst;
import com.ronda.posprinter.base.MyApplication;
import com.ronda.posprinter.utils.CloseUtils;
import com.ronda.posprinter.utils.SPUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/02/28
 * Version: v1.0
 * <p>
 * 蓝牙通信服务
 * 职责：连接蓝牙，获取输入输出流，实现进行通信
 * <p>
 * 蓝牙连接要点：
 * 当前显示设备的连接状态，只能由当前最近传入的连接设备来确定，与上次的连接设备无关。这种情况就是为了避免先后连接两个设备导致显示状态紊乱的情况
 */

public class BluetoothChatService {

    private static final String TAG = BluetoothChatService.class.getSimpleName();

    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private final BluetoothAdapter mBtAdapter;
    private final Handler mHandler;  // 由 UI Activity 通过构造器传过来的
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;


    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;


    private String deviceAddress;

    public BluetoothChatService(Handler handler) {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;

        if (mBtAdapter == null){
            Toast.makeText(MyApplication.getInstance(), "该设备不支持蓝牙", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private synchronized void setState(int state) {
        mState = state;
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(AppConst.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    private synchronized void setState(int state, String deviceName) {
        mState = state;
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(AppConst.MESSAGE_STATE_CHANGE, state, -1, deviceName).sendToTarget();
    }

    public synchronized int getState() {
        return mState;
    }


    /**
     * Stop all threads
     * <p>
     * 取消并置空本类中所有的线程（共3个）
     */
    public void stop() {
        setState(STATE_NONE);
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        Log.i(TAG, "mConnectThread != null --> " + (mConnectThread != null));
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
            Log.i(TAG, "stop --> mConnectThread = null");
        }
    }


    public synchronized void connect(String adrress) {
        if (deviceAddress != null && deviceAddress.equals(adrress) && mState != STATE_NONE) { // 说明再次连接的是同一个设备，并且上次的连接请求还正在进行中或者已经正在通信中
            return;
        }
        deviceAddress = adrress; // 存储最近一次传进来的数据


        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        BluetoothDevice device = mBtAdapter.getRemoteDevice(adrress);
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }


    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_CONNECTED, device.getName());

        //当连接成功时, 持久化存储Mac地址
        SPUtils.putString(AppConst.BLUETOOTH_ADDR, device.getAddress());

        mConnectedThread = new ConnectedThread(socket, device);
        mConnectedThread.start();
    }


    private void sendConnectionFailed() {
        Message msg = mHandler.obtainMessage(AppConst.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(AppConst.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        Log.i(TAG, "sendConnectionFailed");
    }


    private void sendConnectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(AppConst.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(AppConst.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }


    /**
     * 连接时的线程
     */
    private class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;
        private final BluetoothSocket mmSocket;

        private ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        @Override
        public void run() {
            mBtAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                CloseUtils.close(mmSocket);

                // 保证当前连接中断异常的原因是自身的原因(距离太远，蓝牙关闭等)。
                // 并不是由于选择连接了另一个设备而导致的（这种情况不出意外会连接成功，若所以不加判断的话，就会产生实际连接成功，但是却可能显示连接失败的情况）
                // 根本目的就是：保证当前显示设备的连接状态，只能由当前最近传入的连接设备来确定，与上次的连接设备无关。这种情况就是为了避免先后连接两个设备导致显示状态紊乱的情况
                if (mmDevice.getAddress().equals(deviceAddress)) {
                    sendConnectionFailed();
                    setState(STATE_NONE);
                }
                return;
            }
            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // 对外提供的写数据的方法
    public void write(byte[] out) {
        if (mState != STATE_CONNECTED || mConnectedThread == null) return;
        try {
            mConnectedThread.getOutputStream().write(out);
            //mHandler.obtainMessage(AppConst.MESSAGE_WRITE, -1, -1, new String(buffer)).sendToTarget();// 测试的时候可以这样调一下
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void write(int c) {
        if (mState != STATE_CONNECTED || mConnectedThread == null) return;
        try {
            mConnectedThread.getOutputStream().write(c);
            //mHandler.obtainMessage(AppConst.MESSAGE_WRITE, -1, -1, Integer.toHexString(c)).sendToTarget();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String s) {
        if (mState != STATE_CONNECTED || mConnectedThread == null) return;
        try {
            mConnectedThread.getOutputStream().write(s.getBytes("GBK"));
            //mHandler.obtainMessage(AppConst.MESSAGE_WRITE, -1, -1, s).sendToTarget();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 已连接后，通信的线程
     * 其实打印的话，是不需要读数据（必须要子线程），只需要写数据（主线程也可以）。但是又为什么要写ConnectedThread这个类呢？因为可以判断连接状态！！！
     */
    private class ConnectedThread extends Thread {
        private final BluetoothDevice mmDevice;
        private final BluetoothSocket mmSocket;
        private final InputStream mmInputStream;
        private final OutputStream mmOutputStream;

        private ConnectedThread(BluetoothSocket socket, BluetoothDevice device) {
            mmDevice = device;
            this.mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInputStream = tmpIn;
            mmOutputStream = tmpOut;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int len;
            Log.i(TAG, "[ConnectedThread]before while, STATE_CONNECTED --> " + (mState == STATE_CONNECTED) + ", mState" + mState);
            while (mState == STATE_CONNECTED) {
                try {
                    len = mmInputStream.read(buffer);
                    byte[] buf_data = Arrays.copyOf(buffer, len);
                    //mHandler.obtainMessage(AppConst.MESSAGE_READ, -1, -1, buf_data).sendToTarget(); // 读取到的数据不需要发送给Activity（理论上也不会读取到数据）
                } catch (Exception e) {
                    Log.e(TAG, "ConnectedThread --> IOException : " + e.toString() + " mState : " + mState);

                    CloseUtils.close(mmSocket);

                    synchronized (BluetoothChatService.this) {
                        mConnectedThread = null;
                    }

                    // 保证当前连接中断异常的原因是自身的原因(距离太远，蓝牙关闭等)。
                    // 并不是由于选择连接了另一个设备而导致的（这种情况不出意外会连接成功，若所以不加判断的话，就会产生实际连接成功，但是却可能显示连接失败的情况）
                    // 保证当前显示设备的连接状态，只能由当前最近传入的连接设备来确定，与上次的连接设备无关。这种情况就是为了避免先后连接两个设备导致显示状态紊乱的情况
                    if (mmDevice.getAddress().equals(deviceAddress)) {
                        sendConnectionLost();
                        setState(STATE_NONE);
                    }
                    break; // 跳出循环
                }
            }
        }

        public OutputStream getOutputStream() {
            return mmOutputStream;
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}