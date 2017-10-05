package com.ronda.posprinter.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ronda.posprinter.R;
import com.ronda.posprinter.engine.PrintUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class NetPrintActivity extends AppCompatActivity {

    private EditText mEtHost, mEtPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_print);

        mEtHost = (EditText) findViewById(R.id.et_host);
        mEtPort = (EditText) findViewById(R.id.et_port);

        findViewById(R.id.btn_print).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = mEtHost.getText().toString().trim();
                int port = Integer.parseInt(mEtPort.getText().toString().trim());

                try {
                    Socket socket = new Socket(host, port);
                    OutputStream outputStream = socket.getOutputStream();

                    //打印数据
                    byte[] printData = PrintUtils.generateBillData("123456", "0.5");
                    outputStream.write(printData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



    }
}
