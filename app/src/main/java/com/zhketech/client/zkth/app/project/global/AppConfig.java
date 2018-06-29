package com.zhketech.client.zkth.app.project.global;

import android.app.Application;

/**
 * Created by Root on 2018/6/28.
 */

public class AppConfig {

    public AppConfig()

    {
        throw new UnsupportedOperationException("can not construct");
    }


    public  static  int direction = 2;//(1竖屏，2横屏)

    //播放视频是否有声音
    public static  boolean isVideoSound = false;

    //数据编码格式
    public static String dataFormat = "GB2312";
    //数据头
    public static String video_header_id = "ZKTH";

    //登录端口
    public static int server_port = 2010;
    //发送心跳的端口
    public static int heart_port = 2020;
    //
    public static String server_ip = "19.0.0.28";

    public static String current_user = "admin";


    public static String current_pass = "pass";



}
