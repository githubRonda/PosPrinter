package com.ronda.posprinter.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ronda.posprinter.base.MyApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * SharedPreferences 的帮助类
 * <p>
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/11/25
 * Version: v1.0
 */

public class SPUtils {
    private static SharedPreferences preferences = MyApplication.getInstance().getSharedPreferences("config", Context.MODE_PRIVATE);
    private static SharedPreferences.Editor editor = preferences.edit();
    /************************** 保存和读取基本类型的数据 *****************************/
    /**
     * 保存值为 int 类型的数据
     *
     * @param key
     * @param value
     */
    public static void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }
    public static int getInt(String key, int defValue) {
        return preferences.getInt(key, defValue);
    }
    /**
     * 保存值为 String 类型的数据
     *
     * @param key
     * @param value
     */
    public static void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }
    public static String getString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }
    /**
     * 保存值为 boolean 类型的数据
     *
     * @param key
     * @param value
     */
    public static void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }
    public static boolean getBoolean(String key, boolean defvalue) {
        return preferences.getBoolean(key, defvalue);
    }
    /**
     * 保存值为 JavaBean 类型的数据
     * @param tag
     * @param bean
     * @param <T>
     */
    public static <T> void putBean(String tag, T bean) {
        if (null == bean) {
            return;
        }
        Gson gson = new Gson();
        String jsonStr = gson.toJson(bean);
        editor.putString(tag, jsonStr);
        editor.apply();
    }
    public static <T> T getBean(String tag, Class<T> clazz) {
        T bean = new Gson().fromJson(getString(tag, null), clazz);
        return bean;
    }
    /**
     * 保存和读取List<JavaBean>
     * 本质上就是：借助Gson把List集合转成Json字符串存到SharedPreference中;在取的时候，先取出该Json字符串，然后再转成List<JavaBean>
     */
    public static <T> void putList(String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.putString(tag, strJson);
        editor.apply();
    }
    /**
     * @param tag          key
     * @param defaultValue 默认值
     * @param clazz        javaBean数组的字节码。eg:Student[].class
     * @return
     */
    public static <T> List<T> getList(String tag, String defaultValue, Class<T[]> clazz) {
        List<T> datalist = new ArrayList<T>();
        String jsonStr = preferences.getString(tag, defaultValue);
        if (null == jsonStr) {
            return datalist;
        }
        T[] arr = new Gson().fromJson(jsonStr, clazz);
        return Arrays.asList(arr);
    }
    /**
     * 从sp中移除指定节点
     *
     * @param key
     */
    public static void remove(String key) {
        editor.remove(key).apply();
    }
    /**
     * 清除SharedPreference
     */
    public static void clear() {
        editor.clear();
        editor.apply();
    }
}
