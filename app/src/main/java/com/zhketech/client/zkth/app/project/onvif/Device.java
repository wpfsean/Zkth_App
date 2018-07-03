package com.zhketech.client.zkth.app.project.onvif;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *  Device设备信息类
 *
 */

public class Device implements Serializable {

    /**
     * 用户名/密码
     */
    private String userName;
    private String psw;
    //IP地址
    private String ipAddress;

    /**
     * serviceUrl,uuid 通过广播包搜索设备获取
     */
    private String serviceUrl;

    private String name;
    private String devicetype;
    private String channel;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevicetype() {
        return devicetype;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     * getCapabilities
     */
    private String mediaUrl;
    private String ptzUrl;
    private String imageUrl;
    private String eventUrl;
    private String analyticsUrl;

    //RTSP 地址
    private String rtspUrl;
    public String getRtspUrl() {
        return rtspUrl;
    }

    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl;
    }

    /**
     * onvif MediaProfile
     */
    private ArrayList<MediaProfile> profiles;


    public Device() {
        profiles = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        this.ipAddress = serviceUrl.substring(serviceUrl.indexOf("//") + 2, serviceUrl.indexOf("/on"));
    }



    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getPtzUrl() {
        return ptzUrl;
    }

    public void setPtzUrl(String ptzUrl) {
        this.ptzUrl = ptzUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getAnalyticsUrl() {
        return analyticsUrl;
    }

    public void setAnalyticsUrl(String analyticsUrl) {
        this.analyticsUrl = analyticsUrl;
    }

    public ArrayList<MediaProfile> getProfiles() {
        return profiles;
    }

    public void addProfile(MediaProfile profile) {
        this.profiles.add(profile);
    }

    public void addProfiles(ArrayList<MediaProfile> profiles) {
        this.profiles.addAll(profiles);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


    @Override
    public String toString() {
        return "Device{" +
                "userName='" + userName + '\'' +
                ", psw='" + psw + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", serviceUrl='" + serviceUrl + '\'' +
                ", name='" + name + '\'' +
                ", devicetype='" + devicetype + '\'' +
                ", channel='" + channel + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", ptzUrl='" + ptzUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", eventUrl='" + eventUrl + '\'' +
                ", analyticsUrl='" + analyticsUrl + '\'' +
                ", profiles=" + profiles +
                '}';
    }
}

