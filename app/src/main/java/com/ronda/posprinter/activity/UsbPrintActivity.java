package com.ronda.posprinter.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ronda.posprinter.R;
import com.ronda.posprinter.engine.PrintUtils;
import com.ronda.posprinter.engine.usb.USBPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class UsbPrintActivity extends AppCompatActivity {

    private EditText mEtHost, mEtPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_print);

        mEtHost = (EditText) findViewById(R.id.et_host);
        mEtPort = (EditText) findViewById(R.id.et_port);

        USBPrinter.initPrinter(this);

        findViewById(R.id.btn_print).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = PrintUtils.generateBillData("123456", "0.5");
                USBPrinter.getInstance().print(bytes);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        USBPrinter.destroyPrinter();
    }
}
