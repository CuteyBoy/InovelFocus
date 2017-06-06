package com.tv.inovelfocus;

import android.app.Application;
import android.content.Intent;

import com.tv.framework.AppHelper;
import com.tv.inovelfocus.broadcast.ActivateFocusService;

/**
 * 功能描述：调焦app
 * 开发状况：正在开发中
 * 开发作者：黎丝军
 * 开发时间：2017/5/6- 16:37
 */

public class FocusApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, ActivateFocusService.class));
        AppHelper.instance().initCoreApp(this);
    }
}
