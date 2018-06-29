package com.zhketech.client.zkth.app.project.utils;

import android.util.Log;

/**
 * Created by Root on 2018/4/7.
 *
 * Log日志输出工具类
 */

public class Logutils {

    public Logutils(){
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    public static boolean isLog = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    private static final String TAG = "TAG";

    // 下面四个是默认tag的函数
    public static void i(String msg)
    {
        if (isLog)
            Log.i(TAG, msg);
    }

    public static void d(String msg)
    {
        if (isLog)
            Log.d(TAG, msg);
    }

    public static void e(String msg)
    {
        if (isLog)
            Log.e(TAG, msg);
    }

    public static void v(String msg)
    {
        if (isLog)
            Log.v(TAG, msg);
    }

    public static void w(String msg)
    {
        if (isLog)
            Log.w(TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg)
    {
        if (isLog)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg)
    {
        if (isLog)
            Log.i(tag, msg);
    }

    public static void e(String tag, String msg)
    {
        if (isLog)
            Log.i(tag, msg);
    }

    public static void v(String tag, String msg)
    {
        if (isLog)
            Log.i(tag, msg);
    }


}
