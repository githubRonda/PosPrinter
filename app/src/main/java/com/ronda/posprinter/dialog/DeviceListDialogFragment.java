package com.ronda.posprinter.dialog;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ronda.posprinter.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeviceListDialogFragment extends DialogFragment {


    private static final String NO_DEVICE_DATA = "没有搜索到蓝牙设备";

    private Button mBtnSearch;

    private ListView             mListView;
    private ArrayAdapter<String> mListViewAdapter;
    private List<String> mDeviceList = new ArrayList<String>();//对话框的列表数据内容

    private BluetoothAdapter mBtAdapter;

    private Callback mCallback; // 回到接口，用于通信 MAC 地址
    private Activity mActivity; // 宿主Activity


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("请选择连接设备:");

        mActivity = getActivity();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        registerBluetoothReceiver(); // 注册蓝牙相关的广播
        openBluetooth(); // 打开蓝牙
        mBtAdapter.startDiscovery(); // 进行扫描

        return inflater.inflate(R.layout.device_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(R.id.lv);
        mListViewAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mDeviceList);
        mListView.setAdapter(mListViewAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mCallback != null) {
                    mBtAdapter.cancelDiscovery();
                    String info = ((TextView) view).getText().toString();
                    if (NO_DEVICE_DATA.equals(info)){
                        return;
                    }
                    String address = info.substring(info.length() - 17);
                    mCallback.onSelectedItem(address);
                    dismiss();
                }
            }
        });


        mBtnSearch = (Button) view.findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openBluetooth();
                if (mBtAdapter.isDiscovering()) {
                    mBtAdapter.cancelDiscovery();
                    mBtnSearch.setText("再次搜索");
                } else {
                   doDiscovery();
                }
            }
        });

        doDiscovery();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消搜索
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        // 注销广播接收器
        unregisterBluetoothReceiver();
    }

    private void doDiscovery(){
        findPairedDevices();
        mBtAdapter.startDiscovery();
        mBtnSearch.setText("停止搜索");
    }

    /**
     * 开启蓝牙
     */
    public void openBluetooth() {
        if (mBtAdapter != null) {
            if (!mBtAdapter.isEnabled()) {
                //请求用户开启
                //Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivity(intent);
                //直接开启
                mBtAdapter.enable();
            }
        }
    }



    /**
     * 获取手机上已匹配过的蓝牙设备
     */
    private void findPairedDevices() {
        //获取可配对蓝牙设备
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
            mDeviceList.clear();
            mListViewAdapter.notifyDataSetChanged();
        }

        if (pairedDevices.size() > 0) { //存在已经配对过的蓝牙设备
            mDeviceList.clear();
            for (BluetoothDevice device : pairedDevices) {
                String str = device.getName() + ":" + device.getAddress();
                if (!mDeviceList.contains(str)) {
                    mDeviceList.add(str);
                    mListViewAdapter.notifyDataSetChanged();
                }
            }
        } else { //不存在已经配对过的蓝牙设备
            mDeviceList.clear();
            //mDeviceList.add(NO_PAIRED_DEVICES); //没有已匹配过的蓝牙
            mListViewAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 注册蓝牙广播接收器
     */
    private void registerBluetoothReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mActivity.registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 注销广播接收器
     */
    public void unregisterBluetoothReceiver() {
        mActivity.unregisterReceiver(mReceiver);
    }


    public void setCallback(Callback callback) {
        mCallback = callback;
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) { //搜索到新设备
                BluetoothDevice btd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //搜索没有配过对的蓝牙设备
                if (btd.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mDeviceList.add(btd.getName() + ":" + btd.getAddress());
                    mListViewAdapter.notifyDataSetChanged();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) { //搜索结束

                //if (lvDevice.getCount() == 0) {
                if (mListViewAdapter.getCount() == 0) {

                    mDeviceList.add(NO_DEVICE_DATA);
                    mListViewAdapter.notifyDataSetChanged();
                }
                mBtnSearch.setText("再次搜索");
            }
        }
    };


    public interface Callback {
        void onSelectedItem(String address);
    }
}
