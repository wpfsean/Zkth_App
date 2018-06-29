package com.zhketech.client.zkth.app.project.utils;

/**
 * Created by Root on 2018/4/19.
 */

public class ByteUtils {

    public ByteUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    // 注意高低位问题
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8) | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    // int 转byte
    public static byte[] toByteArray(int i) {
        byte[] bt = new byte[4];
        bt[0] = (byte) (0xff & i);
        bt[1] = (byte) ((0xff00 & i) >> 8);
        bt[2] = (byte) ((0xff0000 & i) >> 16);
        bt[3] = (byte) ((0xff000000 & i) >> 24);
        return bt;
    }

    //double 转byte
    public static byte[] getBytes(double data) {
        long intBits = Double.doubleToLongBits(data);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((intBits >> 8 * i) & 0xff);
        }
        return byteRet;
    }

    public static byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }


    public static long bytes2Long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }


    // 以下 是整型数 和 网络字节序的  byte[] 数组之间的转换
    public static byte[] longToBytes(long n) {
        byte[] b = new byte[8];
        b[7] = (byte) (n & 0xff);
        b[6] = (byte) (n >> 8 & 0xff);
        b[5] = (byte) (n >> 16 & 0xff);
        b[4] = (byte) (n >> 24 & 0xff);
        b[3] = (byte) (n >> 32 & 0xff);
        b[2] = (byte) (n >> 40 & 0xff);
        b[1] = (byte) (n >> 48 & 0xff);
        b[0] = (byte) (n >> 56 & 0xff);
        return b;
    }

    public static void longToBytes(long n, byte[] array, int offset) {
        array[7 + offset] = (byte) (n & 0xff);
        array[6 + offset] = (byte) (n >> 8 & 0xff);
        array[5 + offset] = (byte) (n >> 16 & 0xff);
        array[4 + offset] = (byte) (n >> 24 & 0xff);
        array[3 + offset] = (byte) (n >> 32 & 0xff);
        array[2 + offset] = (byte) (n >> 40 & 0xff);
        array[1 + offset] = (byte) (n >> 48 & 0xff);
        array[0 + offset] = (byte) (n >> 56 & 0xff);
    }

    public static long bytesToLong(byte[] array) {
        return ((((long) array[0] & 0xff) << 56)
                | (((long) array[1] & 0xff) << 48)
                | (((long) array[2] & 0xff) << 40)
                | (((long) array[3] & 0xff) << 32)
                | (((long) array[4] & 0xff) << 24)
                | (((long) array[5] & 0xff) << 16)
                | (((long) array[6] & 0xff) << 8)
                | (((long) array[7] & 0xff) << 0));
    }

    public static long bytesToLong(byte[] array, int offset) {
        return ((((long) array[offset + 0] & 0xff) << 56)
                | (((long) array[offset + 1] & 0xff) << 48)
                | (((long) array[offset + 2] & 0xff) << 40)
                | (((long) array[offset + 3] & 0xff) << 32)
                | (((long) array[offset + 4] & 0xff) << 24)
                | (((long) array[offset + 5] & 0xff) << 16)
                | (((long) array[offset + 6] & 0xff) << 8)
                | (((long) array[offset + 7] & 0xff) << 0));
    }

    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static void intToBytes(int n, byte[] array, int offset) {
        array[3 + offset] = (byte) (n & 0xff);
        array[2 + offset] = (byte) (n >> 8 & 0xff);
        array[1 + offset] = (byte) (n >> 16 & 0xff);
        array[offset] = (byte) (n >> 24 & 0xff);
    }

    public static int bytesToInt(byte b[]) {
        return b[3] & 0xff
                | (b[2] & 0xff) << 8
                | (b[1] & 0xff) << 16
                | (b[0] & 0xff) << 24;
    }

//    public static int bytesToInt(byte b[], int offset) {
//        return    b[offset+3] & 0xff
//                | (b[offset+2] & 0xff) << 8
//                | (b[offset+1] & 0xff) << 16
//                | (b[offset] & 0xff) << 24;
//    }

    public static byte[] uintToBytes(long n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);

        return b;
    }

    public static void uintToBytes(long n, byte[] array, int offset) {
        array[3 + offset] = (byte) (n);
        array[2 + offset] = (byte) (n >> 8 & 0xff);
        array[1 + offset] = (byte) (n >> 16 & 0xff);
        array[offset] = (byte) (n >> 24 & 0xff);
    }

    public static long bytesToUint(byte[] array) {
        return ((long) (array[3] & 0xff))
                | ((long) (array[2] & 0xff)) << 8
                | ((long) (array[1] & 0xff)) << 16
                | ((long) (array[0] & 0xff)) << 24;
    }

    public static long bytesToUint(byte[] array, int offset) {
        return ((long) (array[offset + 3] & 0xff))
                | ((long) (array[offset + 2] & 0xff)) << 8
                | ((long) (array[offset + 1] & 0xff)) << 16
                | ((long) (array[offset] & 0xff)) << 24;
    }

    public static byte[] shortToBytes(short n) {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }

    public static void shortToBytes(short n, byte[] array, int offset) {
        array[offset + 1] = (byte) (n & 0xff);
        array[offset] = (byte) ((n >> 8) & 0xff);
    }

    public static short bytesToShort(byte[] b) {
        return (short) (b[1] & 0xff
                | (b[0] & 0xff) << 8);
    }

    public static short bytesToShort(byte[] b, int offset) {
        return (short) (b[offset + 1] & 0xff
                | (b[offset] & 0xff) << 8);
    }

    public static byte[] ushortToBytes(int n) {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }

    public static void ushortToBytes(int n, byte[] array, int offset) {
        array[offset + 1] = (byte) (n & 0xff);
        array[offset] = (byte) ((n >> 8) & 0xff);
    }

    public static int bytesToUshort(byte b[]) {
        return b[1] & 0xff
                | (b[0] & 0xff) << 8;
    }

    public static int bytesToUshort(byte b[], int offset) {
        return b[offset + 1] & 0xff
                | (b[offset] & 0xff) << 8;
    }

    public static byte[] ubyteToBytes(int n) {
        byte[] b = new byte[1];
        b[0] = (byte) (n & 0xff);
        return b;
    }

    public static void ubyteToBytes(int n, byte[] array, int offset) {
        array[0] = (byte) (n & 0xff);
    }

    public static int bytesToUbyte(byte[] array) {
        return array[0] & 0xff;
    }

    public static int bytesToUbyte(byte[] array, int offset) {
        return array[offset] & 0xff;
    }
    // char 类型、 float、double 类型和 byte[] 数组之间的转换关系还需继续研究实现。


    //截取byte
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i = begin; i < begin + count; i++)
            bs[i - begin] = src[i];
        return bs;
    }


    //Get0的下标(用于解析sipServer,sipName,sipPass)
    public static int getPosiotion(byte[] sipServer){
        int temp = 0;
        for(int i = 0; i< sipServer.length;i++){
            if (sipServer[i] == 0) {
                return i;
            }
        }
        return temp;
    }

}
