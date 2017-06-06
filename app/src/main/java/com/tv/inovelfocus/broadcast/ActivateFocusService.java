package com.tv.inovelfocus.broadcast;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * 功能描述：激活焦点界面活动
 * 开发状况：正在开发中
 * 开发作者：黎丝军
 * 开发时间：2017/5/8- 22:09
 */

public class ActivateFocusService extends Service {

    //接收激活广播
    private ActivateFocusReceiver mReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mReceiver = new ActivateFocusReceiver();
        registerReceiver(mReceiver, getFilter());
    }

    /**
     * 过滤器
     * @return 实例
     */
    private IntentFilter getFilter() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ActivateFocusReceiver.ACTION_FOCUS);
        return filter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
