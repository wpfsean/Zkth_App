package com.zhketech.client.zkth.app.project.utils;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Xml;


import com.zhketech.client.zkth.app.project.beans.DeviceInfor;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Root on 2018/6/22.
 */

public class OnvifUtils implements Runnable {
    //GetServices
    String getServices = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tds=\"http://www.onvif.org/ver10/device/wsdl\" xmlns:tt=\"http://www.onvif.org/ver10/schema\">\n" +
            "  <s:Header xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
            "    <wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">\n" +
            "      <wsse:UsernameToken>\n" +
            "        <wsse:Username>%s</wsse:Username>\n" +
            "        <wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest\">%s</wsse:Password>\n" +
            "        <wsse:Nonce>%s</wsse:Nonce>\n" +
            "        <wsu:Created>%s</wsu:Created>\n" +
            "      </wsse:UsernameToken>\n" +
            "    </wsse:Security>\n" +
            "  </s:Header>\n" +
            "  <soap:Body>\n" +
            "    <tds:GetServices>\n" +
            "      <tds:IncludeCapability>false</tds:IncludeCapability>\n" +
            "    </tds:GetServices>\n" +
            "  </soap:Body>\n" +
            "</soap:Envelope>";
    //GetCapabilities
    public static final String getCapabilities = "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\"><s:Header><Security s:mustUnderstand=\"1\" xmlns=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\"><UsernameToken><Username>%s</Username><Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest\">%s</Password><Nonce EncodingType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary\">%s</Nonce><Created xmlns=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">%s</Created></UsernameToken></Security></s:Header><s:Body xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><GetCapabilities xmlns=\"http://www.onvif.org/ver10/device/wsdl\"><Category>All</Category></GetCapabilities></s:Body></s:Envelope>";
    //getProfiles
    public static final String GET_PROFILES = "<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\"><s:Header><wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"><wsse:UsernameToken><wsse:Username>%s</wsse:Username><wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest\">%s</wsse:Password><wsse:Nonce>%s</wsse:Nonce><wsu:Created>%s</wsu:Created></wsse:UsernameToken></wsse:Security></s:Header><s:Body xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><GetProfiles xmlns=\"http://www.onvif.org/ver10/media/wsdl\"></GetProfiles></s:Body></s:Envelope>";

    //getRtsp
    public static final String GET_URI_BODY = "<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\"><s:Header><wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"><wsse:UsernameToken><wsse:Username>%s</wsse:Username><wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest\">%s</wsse:Password><wsse:Nonce>%s</wsse:Nonce><wsu:Created>%s</wsu:Created></wsse:UsernameToken></wsse:Security></s:Header><s:Body xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><GetStreamUri xmlns=\"http://www.onvif.org/ver10/media/wsdl\"><StreamSetup><Stream xmlns=\"http://www.onvif.org/ver10/schema\">RTP-Unicast</Stream><Transport xmlns=\"http://www.onvif.org/ver10/schema\"><Protocol>RTSP</Protocol></Transport></StreamSetup><ProfileToken>%s</ProfileToken></GetStreamUri></s:Body></s:Envelope>";
    private OnHttpSoapListener mListener;
    private HttpURLConnection con;
    private DeviceInfor mDeviceInfor;
    private String mCreated, mNonce, mAuthPwd;

    public OnvifUtils(DeviceInfor mDeviceInfor, OnHttpSoapListener mListener) {
        this.mDeviceInfor = mDeviceInfor;
        this.mListener = mListener;
        createAuthString();
    }


    @Override
    public void run() {
        synchronized ("") {

            //通过getservices获取meida
            try {
                String media_Url = getMediaUrlByGetServices(mDeviceInfor.getServiceURL());
                if (TextUtils.isEmpty(media_Url)) {
                    //通过getCapabilities获取media
                    media_Url = getMediaUrlByGetCapabilities(mDeviceInfor.getServiceURL());
                }
                if (TextUtils.isEmpty(media_Url)) {
                    mListener.OnHttpSoapDone(mDeviceInfor,"未获取到mediaUrl", false);
                    return;
                }
                String profile = getProfile(media_Url);
                if (TextUtils.isEmpty(profile)){
                    mListener.OnHttpSoapDone(mDeviceInfor,"未获取到Token", false);
                    return;
                }
                String uri = getRtsp(media_Url, profile);
                if (TextUtils.isEmpty(uri)){
                    mListener.OnHttpSoapDone(mDeviceInfor,"未获取到Rtsp", false);
                    return;
                }

                mListener.OnHttpSoapDone(mDeviceInfor,uri, true);

            } catch (IOException e) {
                mListener.OnHttpSoapDone(mDeviceInfor, e.getMessage(), false);
            }

//            if (TextUtils.isEmpty(media_Url)) {
//                //通过getCapabilities获取media
//                media_Url = getMediaUrlByGetCapabilities(mDeviceInfor.getServiceURL());
//            }
//            if (TextUtils.isEmpty(media_Url)) {
//                return;
//            }
////            //通过media获取token
//            String profile = getProfile(media_Url);
//            String uri = getRtsp(media_Url, profile);
//            if (!TextUtils.isEmpty(uri)){
//                if (mListener != null){
//                    mListener.OnHttpSoapDone(mDeviceInfor,uri,true);
//                }
//            }
        }
    }

    //通过getservices获取media_url
    public String getMediaUrlByGetServices(String url) throws IOException {
        String mediaUrl = "";
        try {
            initConn(url);
            con.connect();
            String content = String.format(getServices,
                    mDeviceInfor.username, mAuthPwd, mNonce, mCreated);
            con.getOutputStream().write(content.getBytes());
            InputStream inStream = con.getInputStream();
            String res = inputToStr(inStream);
            mediaUrl = findMediaByXml(res);
        } catch (IOException e) {
            if (mListener != null) {
                mListener.OnHttpSoapDone(mDeviceInfor, "http error:" + con.getResponseCode(), false);
            }
            return "";
        }
        return mediaUrl;
    }

    //通过getCapabilities获取media_url
    public String getMediaUrlByGetCapabilities(String url) throws IOException {
        String mediaUrl = "";
        try {
            initConn(url);
            con.connect();
            String content = String.format(getCapabilities,
                    mDeviceInfor.username, mAuthPwd, mNonce, mCreated);
            con.getOutputStream().write(content.getBytes());
            InputStream inStream = con.getInputStream();
            String res = inputToStr(inStream);
            mediaUrl = findMediaByXml(res);
        } catch (IOException e) {
            if (mListener != null) {
                mListener.OnHttpSoapDone(mDeviceInfor, "http error:" + con.getResponseCode(), false);
                return "";
            }
        }
        return mediaUrl;
    }


    //网络请求
    private void initConn(String url) throws IOException {
        URL url1 = new URL(url);
        con = (HttpURLConnection) url1.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setUseCaches(false);
//        con.setReadTimeout(2 * 1000);
//        con.setConnectTimeout(2 * 1000);
        con.setInstanceFollowRedirects(true);
        con.setRequestProperty("Content-Type",
                "application/soap+xml; charset=utf-8");
    }

    //生成必要的参数
    private void createAuthString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.CHINA);
        mCreated = df.format(new Date());
        mNonce = getNonce();
        mAuthPwd = getPasswordEncode(mNonce, mDeviceInfor.password, mCreated);
    }

    //随机数据
    public String getNonce() {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 24; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    //密码加密
    public String getPasswordEncode(String nonce, String password, String date) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // 从官方文档可以知道我们nonce还需要用Base64解码一次
            byte[] b1 = Base64.decode(nonce.getBytes(), Base64.DEFAULT);
            // 生成字符字节流
            byte[] b2 = date.getBytes(); // "2013-09-17T09:13:35Z";
            byte[] b3 = password.getBytes();
            // 根据我们传得值的长度生成流的长度
            byte[] b4 = new byte[b1.length + b2.length + b3.length];
            // 利用sha-1加密字符
            md.update(b1, 0, b1.length);
            md.update(b2, 0, b2.length);
            md.update(b3, 0, b3.length);
            // 生成sha-1加密后的流
            b4 = md.digest();
            // 生成最终的加密字符串
            String result = new String(Base64.encode(b4, Base64.DEFAULT));
            return result.replace("\n", "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //inputstream 转string
    public String inputToStr(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    //通过xml获取mediaUrl和ptz_Url
    public String findMediaByXml(String xml) {
        List<String> mediaList = new ArrayList<>();
        List<String> ptzList = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        try {
            parser.setInput(input, "UTF-8");
            int eventType = parser.getEventType();
            boolean done = false;
            while (eventType != XmlPullParser.END_DOCUMENT || done) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("Service")) {
                            eventType = parser.next();
                            String nameSpace = parser.nextText();
                            if (nameSpace.equals("http://www.onvif.org/ver10/media/wsdl")) {
                                eventType = parser.next();
                                String media_service = parser.nextText();
                                if (!TextUtils.isEmpty(media_service)) {
                                    mediaList.add(media_service);
                                }
                            }

                            if (nameSpace.equals("http://www.onvif.org/ver20/ptz/wsdl")) {
                                eventType = parser.next();
                                String ptz_service = parser.nextText();
                                if (!TextUtils.isEmpty(ptz_service)) {
                                    ptzList.add(ptz_service);
                                    mDeviceInfor.setSuporrtPtz(true);
                                    mDeviceInfor.setServiceURL(ptz_service);
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
            return mediaList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //获取profile
    public String getProfile(String mediaUrl) {
        String profile = "";
        try {
            initConn(mediaUrl);
            con.connect();
            String content = String.format(GET_PROFILES,
                    mDeviceInfor.username, mAuthPwd, mNonce, mCreated);
            con.getOutputStream().write(content.getBytes());
            InputStream inStream = con.getInputStream();
            String res = inputToStr(inStream);
            profile = getTokenByProfile(res);

        } catch (IOException e) {
        }
        return profile;
    }

    //获取最后的rtsp
    public String getRtsp(String mediaUrl, String profile) {
        String uri = "";
        try {
            initConn(mediaUrl);
            con.connect();
            String content = getURIContent(profile);
            con.getOutputStream().write(content.getBytes());
            InputStream inStream = con.getInputStream();
            String res = inputToStr(inStream);
            uri = getRtspByXml(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    //获取 token
    private String getTokenByProfile(String xml) {
        int position = 0;
        XmlPullParser parser = Xml.newPullParser();
        ArrayList<String> profiles = new ArrayList<>();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        try {
            parser.setInput(input, "UTF-8");
            int eventType = parser.getEventType();
            boolean done = false;
            while (eventType != XmlPullParser.END_DOCUMENT || done) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("Profiles")) {
                            String token = parser.getAttributeValue(null, "token");
                            profiles.add(token);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String channel = mDeviceInfor.getChannel();
            position = Integer.parseInt(channel);
        } catch (Exception e) {
            position = 0;
        }
        if (profiles.size() > 0) {
            String mToken = profiles.get(position * 2 - 1);
            mDeviceInfor.setToken(mToken);
            return profiles.get(position * 2 - 1);
        } else {
            return "";
        }
    }

    //获取RtspUrl需要的参数
    private String getURIContent(String profile) {
        String content = String.format(GET_URI_BODY, mDeviceInfor.username,
                mAuthPwd, mNonce, mCreated, profile);
        return content;
    }


    //getRtsp
    public String getRtspByXml(String xml) {
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        try {
            parser.setInput(input, "UTF-8");
            int eventType = parser.getEventType();
            boolean done = false;
            while (eventType != XmlPullParser.END_DOCUMENT || done) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("Uri")) {
                            eventType = parser.next();
                            return parser.getText();
                        }
                        break;
                    case XmlPullParser.END_TAG:

                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public interface OnHttpSoapListener {
        public void OnHttpSoapDone(DeviceInfor camera, String uri, boolean isSuccess);
    }

}
