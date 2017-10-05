package com.ronda.posprinter.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/07/29
 * Version: v1.0
 * <p>
 * 关闭实现了Closeable接口的类，一般用于释放资源（流，socket，cursor等）
 */

public class CloseUtils {
    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}