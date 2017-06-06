package com.tv.inovelfocus.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tv.inovelfocus.activity.MainActivity;

/**
 * 功能描述：激活焦点界面
 * 开发状况：正在开发中
 * 开发作者：黎丝军
 * 开发时间：2017/5/6- 16:59
 */

public class ActivateFocusReceiver extends BroadcastReceiver {

    //焦点活动
    protected static final String ACTION_FOCUS = "com.inovel.intent.action.FOCUS";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ACTION_FOCUS.equals(intent.getAction())) {
            Log.d(getClass().getSimpleName(),"启动焦点调试主界面");
            final Intent mainActivity = new Intent(context, MainActivity.class);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivity);
        }
    }
}
