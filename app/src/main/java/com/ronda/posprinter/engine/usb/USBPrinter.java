package com.ronda.posprinter.engine.usb;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.socks.library.KLog;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * USB打印机
 * Created by john on 17-5-10.
 */

public class USBPrinter {

    private static final String ACTION_USB_PERMISSION = "com.usb.printer.USB_PERMISSION";

    private static USBPrinter mInstance;

    private Context mContext;
    private UsbDevice mUsbDevice;

    private UsbManager mUsbManager;
    private UsbDeviceConnection mUsbDeviceConnection;

    private BroadcastReceiver mUsbDeviceReceiver;

    private USBPrinter() {

    }

    public static USBPrinter getInstance() {
        if (mInstance == null) {
            mInstance = new USBPrinter();
        }
        return mInstance;
    }

    /**
     * 初始化打印机，需要与destroy对应
     *
     * @param context 上下文
     */
    public static void initPrinter(Context context) {
        getInstance().init(context);
    }

    /**
     * 销毁打印机持有的对象
     */
    public static void destroyPrinter() {
        getInstance().destroy();
    }

    private void init(Context context) {
        mContext = context;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);


        mUsbDeviceReceiver = new UsbDeviceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION); // 请求权限的Action
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED); // 断开连接的Action
        mContext.registerReceiver(mUsbDeviceReceiver, filter);

        // 列出所有的USB设备，并且都请求获取USB权限
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);

        System.out.println(deviceList);

        for (UsbDevice device : deviceList.values()) {

            mUsbManager.requestPermission(device, mPermissionIntent);

            int vendorId = device.getVendorId();
            int productId = device.getProductId();
            int deviceClass = device.getDeviceClass();
            int deviceSubclass = device.getDeviceSubclass();
            int deviceProtocol = device.getDeviceProtocol();

            String deviceName = device.getDeviceName();

            KLog.i("vendorId: " + vendorId + ", productId: " + productId + ", deviceClass: " + deviceClass + ", deviceSubclass: " +
                    deviceSubclass + ", deviceProtocol: " + deviceProtocol + ", deviceName: " + deviceName);
        }
    }

    private void destroy() {
        mContext.unregisterReceiver(mUsbDeviceReceiver);

        if (mUsbDeviceConnection != null) {
            mUsbDeviceConnection.close();
            mUsbDeviceConnection = null;
        }

        mContext = null;
        mUsbManager = null;
    }


    public List<UsbDevice> getUsbDeviceList() {
        // 把Collection 集合转成List集合
//        Collection<UsbDevice> collection = mUsbManager.getDeviceList().values();
//        ArrayList<UsbDevice> list = new ArrayList<>();
//        for (UsbDevice device : collection) {
//            list.add(device);
//        }

        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Set<String> strings = deviceList.keySet();

        return null;
    }

    public void requestPermission(UsbDevice device) {
        mUsbDevice = null;
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        mUsbManager.requestPermission(device, mPermissionIntent);
    }

    /**
     * 打印方法
     *
     * @param msg
     */
    public void print(String msg) {
        if (msg == null) {
            Toast.makeText(mContext, "打印数据不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            print(msg.getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void print(byte[] data) {
        if (data == null) {
            Toast.makeText(mContext, "打印数据不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        final byte[] printData = data;
        if (mUsbDevice != null) {
            UsbInterface usbInterface = mUsbDevice.getInterface(0);
            for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                final UsbEndpoint ep = usbInterface.getEndpoint(i);
                if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                        mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
                        if (mUsbDeviceConnection != null) {
                            Toast.makeText(mContext, "Device connected", Toast.LENGTH_SHORT).show();
                            mUsbDeviceConnection.claimInterface(usbInterface, true);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    int b = mUsbDeviceConnection.bulkTransfer(ep, printData, printData.length, 100000);
                                    Log.i("Return Status", "b-->" + b);

                                    //mUsbDeviceConnection.releaseInterface(usbInterface);
                                }
                            }).start();

                            //mUsbDeviceConnection.releaseInterface(usbInterface);
                            break;
                        }
                    }
                }
            }
        } else {
            Toast.makeText(mContext, "No available USB print device", Toast.LENGTH_SHORT).show();
        }
    }


    class UsbDeviceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbDevice != null) {
                            mUsbDevice = usbDevice;
                        }
                    } else {
                        Toast.makeText(context, "Permission denied for device " + usbDevice, Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (mUsbDevice != null) {
                    Toast.makeText(context, "Device closed", Toast.LENGTH_SHORT).show();
                    if (mUsbDeviceConnection != null) {
                        mUsbDeviceConnection.close();
                    }
                }
            }
        }
    }

}
