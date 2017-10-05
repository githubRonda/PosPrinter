package com.ronda.posprinter.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ronda.posprinter.R;
import com.ronda.posprinter.engine.PrintUtils;
import com.ronda.posprinter.engine.serialport.SerialPortFinder;
import com.ronda.posprinter.engine.serialport.SerialPortService;
import com.ronda.posprinter.view.LSpinner;

/**
 * 串口打印
 */
public class SerialPrintActivity extends AppCompatActivity implements View.OnClickListener {

    private LSpinner<String> mSpinnerDevice, mSpinnerBaudrate;

    private Button mBtnSend;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_print);

        initView();
    }

    private void initView() {
        mSpinnerDevice = (LSpinner<String>) findViewById(R.id.spinner_device);
        mSpinnerBaudrate = (LSpinner<String>) findViewById(R.id.spinner_baudrate);
        mBtnSend = (Button) findViewById(R.id.btn_send);

        mSpinnerDevice.setData(new SerialPortFinder().getAllDevicesPath());
        mSpinnerBaudrate.setData(getResources().getStringArray(R.array.baudrates));

        mBtnSend.setOnClickListener(this);
    }


    //===============OnClickListener================
    @Override
    public void onClick(View v) {

        //打开串口
        String devicePath = mSpinnerDevice.getSelectedItem(); // 设备路径
        int baudrate = Integer.parseInt(mSpinnerBaudrate.getSelectedItem()); // 波特率

        if (!devicePath.startsWith("/dev/ttys")) {
            Toast.makeText(getApplicationContext(), "打印串口设置有误！", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建 SerialPortService
        SerialPortService mSerialPortService = new SerialPortService(devicePath, baudrate);

        // 如果打开成功,则打印数据；否则提示失败
        if (mSerialPortService.isActive()) {
            byte[] printData = PrintUtils.generateBillData("123456", "0.5");
            mSerialPortService.write(printData);

            // 这里可以每次打印完成后，关闭串口，节省资源。不关闭也可以。看个人喜好
            mSerialPortService.closeSerial();
            mSerialPortService = null;
        }else{
            Toast.makeText(getApplicationContext(), "打印串口不能使用", Toast.LENGTH_SHORT).show();
            mSerialPortService.closeSerial();
            mSerialPortService = null;
        }

    }
}
