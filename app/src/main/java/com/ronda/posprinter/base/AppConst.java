package com.ronda.posprinter.base;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/02/28
 * Version: v1.0
 * <p>
 * 定义了一些常量
 */

public interface AppConst {


    // Message types sent from the BluetoothChatService Handler
    int MESSAGE_STATE_CHANGE = 1; // 默认都是public static final
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5; // 连接时失败 和 通讯过程中 中断 的情况. 表示 message 中的 what 值
    // Key names received from the BluetoothChatService Handler
    String TOAST = "toast";


    //todo======================SharedPreferences相关==================================
    /**
     * 是否开启更新的key
     */
    String BLUETOOTH_ADDR = "bluetooth_addr";

}