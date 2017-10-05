package com.ronda.posprinter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ronda.posprinter.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        findViewById(R.id.btn_bt_print).setOnClickListener(this);
        findViewById(R.id.btn_sp_print).setOnClickListener(this);
        findViewById(R.id.btn_net_print).setOnClickListener(this);
        findViewById(R.id.btn_usb_print).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_bt_print://蓝牙打印
                startActivity(new Intent(this, BluetoothPrintActivity.class));
                break;
            case R.id.btn_sp_print://串口打印
                startActivity(new Intent(this, SerialPrintActivity.class));
                break;
            case R.id.btn_net_print://网口打印
                startActivity(new Intent(this, NetPrintActivity.class));
                break;
            case R.id.btn_usb_print://USB打印
                startActivity(new Intent(this, UsbPrintActivity.class));
                break;
        }
    }
}
