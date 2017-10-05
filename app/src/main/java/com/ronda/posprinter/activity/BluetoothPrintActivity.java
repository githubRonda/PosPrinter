package com.ronda.posprinter.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.ronda.posprinter.base.AppConst;
import com.ronda.posprinter.engine.bluetooth.BluetoothChatService;
import com.ronda.posprinter.dialog.DeviceListDialogFragment;
import com.ronda.posprinter.R;
import com.ronda.posprinter.engine.PrintUtils;
import com.ronda.posprinter.utils.QRCodeUtil;
import com.ronda.posprinter.utils.SPUtils;
import com.socks.library.KLog;

/**
 * 蓝牙打印
 */
public class BluetoothPrintActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_ENABLE_BT = 0;
    private BluetoothChatService mChatService;

    private TextView mTextView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_print);

        initView();

        mChatService = new BluetoothChatService(mHandler);

        if (BluetoothAdapter.getDefaultAdapter() == null){
            Toast.makeText(this, "该设备不支持蓝牙！！", Toast.LENGTH_SHORT).show();
            return ;
        }

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else{
            connectBluetooth();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatService.stop();
    }

    private void connectBluetooth(){
        String addr = SPUtils.getString(AppConst.BLUETOOTH_ADDR, "");

        // 连接蓝牙时，会有对话框提示，所以不能在onCreate()中，只能是所有View绘制完之后才可以
        if (addr.isEmpty()) {
            showDialog();
        } else {//自动连接蓝牙
            mChatService.connect(addr);
        }
    }


    private void initView() {
        mTextView = (TextView) findViewById(R.id.tv_state);
        mImageView = (ImageView) findViewById(R.id.iv_qrcode);

        findViewById(R.id.btn_disconnect).setOnClickListener(this);
        findViewById(R.id.btn_connect).setOnClickListener(this);
        findViewById(R.id.btn_print).setOnClickListener(this);

        try {
            Bitmap bitmap = QRCodeUtil.createQRCode("123456", 100);
            mImageView.setImageBitmap(bitmap);
            KLog.e("width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight() + ", count: " + bitmap.getByteCount());
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:
                showDialog();
                break;
            case R.id.btn_disconnect:
                mChatService.stop();
                break;
            case R.id.btn_print:
                //Toast.makeText(this, "btn_print", Toast.LENGTH_SHORT).show();
                startPrint();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT){
            if (resultCode == Activity.RESULT_OK){
                connectBluetooth();
            }
        }
    }

    /**
     * 显示蓝牙列表对话框
     */
    private void showDialog() {
        DeviceListDialogFragment dialog = new DeviceListDialogFragment();
        dialog.setCallback(new DeviceListDialogFragment.Callback() {
            @Override
            public void onSelectedItem(String address) {
                // 连接远程蓝牙
                mChatService.connect(address);
            }
        });
        dialog.show(getSupportFragmentManager(), "deviceListDialogFragment");
    }

    /**
     * 打印数据
     */
    private void startPrint() {

        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "蓝牙未连接:)", Toast.LENGTH_SHORT).show();
            return;
        }

//        byte[] printData = PrintUtils.generateBillData("123456", "0.5");
        byte[] printData = PrintUtils.generateQRCodeData("http://www.baidu.com");

        mChatService.write(printData);
    }


    private final Handler mHandler = new MyHandler();

    /**
     * 实际中，我们只需要 MESSAGE_STATE_CHANGE 这个信息，便于更新界面中的蓝牙连接状态。
     * 对于 MESSAGE_WRITE 和  MESSAGE_READ 这两个信息便于在调试的时候使用
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConst.MESSAGE_STATE_CHANGE:
                    if (msg.arg1 == BluetoothChatService.STATE_CONNECTED) {
                        String deviceName = msg.obj.toString();
                        //mTvState.setText(msg.arg1 + "_" + deviceName);
                        mTextView.setText("已连接至" + deviceName);
                    } else if (msg.arg1 == BluetoothChatService.STATE_NONE) {
                        mTextView.setText("未连接");
                    } else if (msg.arg1 == BluetoothChatService.STATE_CONNECTING) {
                        mTextView.setText("连接中...");
                    }
                    break;
//                case AppConst.MESSAGE_WRITE:
//                    String writeMessage = (String) msg.obj;
//                    KLog.i("write ==> " + writeMessage);
//                    break;
//                case AppConst.MESSAGE_READ:
//                    byte[] buf = (byte[]) msg.obj;
//                    String readTxt = HexUtils.bytesToHexString(buf); // 把字节数组转成16进制字符串
//                    KLog.i("read ==> " + readTxt);
//                    break;
            }
        }

    }


}
