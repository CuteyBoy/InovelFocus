package com.tv.inovelfocus.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.tv.framework.AppHelper;
import com.tv.framework.activity.AbsBaseActivity;
import com.tv.framework.utils.ToastUtil;
import com.tv.inovelfocus.R;
import com.tv.inovelfocus.bll.Device;
import com.tv.inovelfocus.bll.FocusUtil;
import com.tv.inovelfocus.util.DecodeUtil;
import com.tv.inovelfocus.view.FocusAnimView;
import com.tv.serialport.IByteParam;
import com.tv.serialport.ISerialPortCallback;
import com.tv.serialport.SerialPortManager;

public class MainActivity extends AbsBaseActivity
        implements ISerialPortCallback{

    //单位时间
    private static final long UNIT = 1000;
    //结束activity时间
    private static final long FINISH_TIME = 5 * UNIT;
    //移除步长值
    private static final int REMOVE_FOCUS = 20;
    //焦点视图
    private FocusAnimView mFocusIv;
    //左是否结束
    private boolean isLeftEnd = false;
    //右是否结束
    private boolean isRightEnd = false;
    //串口管理器
    protected SerialPortManager mManager;
    //用于判断是否打开串口成功
    private boolean isOpenPortSuccess = false;
    //是否自动调焦完成
    private boolean isAutoFocusEnd = false;
    //计时器
    private CountDownTimer mDownTimer = new CountDownTimer(FINISH_TIME,UNIT) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            mManager.onDestroy();
            AppHelper.instance().exitApp();
        }
    };

    @Override
    public void onCreateView() {
        setContentView(R.layout.activity_main);
        mFocusIv = (FocusAnimView) findViewById(R.id.fav_focus);
    }

    @Override
    public void onInitObjects() {
        mManager = SerialPortManager.instance();
    }

    @Override
    public void onSetListeners() {
        mManager.setSerialPortCallback(this);
    }

    @Override
    public void onInitData(Bundle savedInstanceState) {
        mManager.openSerialPort(Device.PATH,Device.BAUDRATE);
        mFocusIv.setText(R.string.inovel);
        registerHomeReceiver();
    }

    @Override
    public void onSuccess() {
        isOpenPortSuccess = true;
        mManager.sendMessage(FocusUtil.getAutoFocusParam());
        mFocusIv.startAnim(5 * 1000, 3);
    }

    @Override
    public void onError(String errorInfo) {
        isAutoFocusEnd = true;
        ToastUtil.toast(this,errorInfo);
    }

    @Override
    public void onReceiveData(IByteParam param) {
        if(FocusUtil.isAutoFocusSuccess(param)) {
            mDownTimer.start();
            isAutoFocusEnd = true;
            ToastUtil.toast(this,"自动调焦完成");
        } else if(FocusUtil.isLeftFocusMax(param)) {
            isLeftEnd = true;
            ToastUtil.toast(this,"左调焦，已经到底了");
        } else if(FocusUtil.isRightFocusMin(param)) {
            isRightEnd = true;
            ToastUtil.toast(this,"右调焦，已经到底了");
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(mFocusIv.isAnimFinish() && isOpenPortSuccess) {
                    mDownTimer.start();
                }
                break;
            default:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                isRightEnd = false;
                if(mFocusIv.isAnimFinish() && isOpenPortSuccess && !isLeftEnd) {
                    cancelCountDown();
                    mFocusIv.rightRotate();
                    mManager.sendMessage(FocusUtil.getLeftFocusParam(REMOVE_FOCUS));
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                isLeftEnd = false;
                if(mFocusIv.isAnimFinish() && isOpenPortSuccess && !isRightEnd) {
                    cancelCountDown();
                    mFocusIv.leftRotate();
                    mManager.sendMessage(FocusUtil.getRightFocusParam(REMOVE_FOCUS));
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                if(isAutoFocusEnd) {
                    if(!mFocusIv.isAnimFinish()) {
                        mFocusIv.stopAnim(true);
                    }
                    if(isOpenPortSuccess) {
                        cancelCountDown();
                        mManager.onDestroy();
                    }
                    AppHelper.instance().exitApp();
                } else {
                    return false;
                }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 取消倒计时
     */
    private void cancelCountDown() {
        if(mDownTimer != null) {
            mDownTimer.cancel();
        }
    }

    /**
     * 注册监听Home的广播
     */
    private void registerHomeReceiver() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeReceiver, intentFilter);
    }

    /**
     * Home键接收器
     */
    private final BroadcastReceiver mHomeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                String reason = intent.getStringExtra("reason");
                if(reason != null) {
                    if (reason.equals("homekey")) {
                        AppHelper.instance().exitApp();
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mHomeReceiver);
    }
}
