package com.tv.inovelfocus.bll;

import com.tv.serialport.ByteParam;
import com.tv.serialport.IByteParam;

/**
 * 功能描述：对焦指令
 * 开发状况：正在开发中
 * 开发作者：黎丝军
 * 开发时间：2017/5/6- 14:03
 */

public class FocusUtil {

    //临时byte参数
    private static IByteParam mTempParam;

    /**
     * 获取自动对焦命令
     * @return 参数
     */
    public static byte[] getAutoFocusParam() {
        return getFocusParam(0x02,0x00);
    }

    /**
     * 0xff 0x03 0x00
     * 获取左移动参数
     * @return 返回参数
     */
    public static byte[] getLeftFocusParam(int moveValue) {
        return getFocusParam(0x03,moveValue);
    }

    /**
     * 0xff 0x04 0x00
     * 获取左移动参数
     * @return 返回参数
     */
    public static byte[] getRightFocusParam(int moveValue) {
        return getFocusParam(0x04,moveValue);
    }

    /**
     * 获取调焦参数
     * @param key 指令键
     * @param value 指令值
     * @return 返回命令参数
     */
    private static IByteParam getFocusByteParam(int key,int value) {
        mTempParam = new ByteParam(3);
        mTempParam.putInt(0xff);
        mTempParam.putInt(key);
        mTempParam.putInt(value);
        return mTempParam;
    }

    /**
     * 获取调焦参数
     * @param key 指令键
     * @param value 指令值
     * @return 返回命令参数
     */
    private static byte[] getFocusParam(int key,int value) {
        return getFocusByteParam(key,value).getData();
    }

    /**
     * 判读是否到达左边最大位置
     * @return true表示最大
     */
    public static boolean isLeftFocusMax(IByteParam param) {
        return focusEquals(0x0A, 0x01, param);
    }

    /**
     * 判读是否到达右边最小位置
     * @return true表示最小
     */
    public static boolean isRightFocusMin(IByteParam param) {
        return focusEquals(0x0A,0x02, param);
    }

    /**
     * 判断是否自动对焦成功
     * @param param 参数
     * @return true表示成功
     */
    public static boolean isAutoFocusSuccess(IByteParam param) {
        if((param.getByte(0) == 0x05 && param.getByte(1) == 0x00
                && param.getByte(2) == 0x00) ||
                (param.getByte(0) == 0x0a && param.getByte(1) == 0x05 && param.getByte(2) == 0x00)) {
            return true;
        }
        return false;
    }

    /**
     * 判断焦点是否调整到最大还是最小
     * @param value 判断值
     * @param param 比较参数
     * @return true表示到达
     */
    private static boolean focusEquals(int key,int value,IByteParam param) {
        mTempParam = getFocusByteParam(key,value);
        if(mTempParam.getByte(1) == param.getByte(1) &&
                mTempParam.getByte(2) == param.getByte(2)) {
            return true;
        }
        return false;
    }

    private FocusUtil() {
    }
}
