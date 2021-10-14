package com.example.longtouch;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.List;

public class FloatingService extends Service {
    private MyBinder binder;
    private FloatWindowUtil mFloatWindowUtil;
    private CmdUtil cmdUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        cmdUtil = new CmdUtil(this);
        mFloatWindowUtil = new FloatWindowUtil(this);
        mFloatWindowUtil.setCmdUtil(cmdUtil);
        binder = new MyBinder(mFloatWindowUtil);
        MainActivity.MyLog.logFromUi("服务创建");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MainActivity.MyLog.logFromUi("服务启动");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        MainActivity.MyLog.logFromUi("服务绑定");
        if (null != binder)
            return binder;
        return null;
    }

    static class MyBinder extends Binder {
        FloatWindowUtil floatWindowUtil;

        public MyBinder(FloatWindowUtil floatWindowUtil) {
            this.floatWindowUtil = floatWindowUtil;
        }

        public void show(){
            if(floatWindowUtil.isShow){
                MainActivity.MyLog.logFromUi("悬浮窗隐藏");
                floatWindowUtil.hideFloatingWindow();
            }else{
                MainActivity.MyLog.logFromUi("悬浮窗显示");
                floatWindowUtil.showFloatingWindow();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFloatWindowUtil.hideFloatingWindow();
        cmdUtil.close();
        MainActivity.MyLog.logFromUi("服务销毁");
    }

    public static boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList =activityManager.getRunningServices(Integer.MAX_VALUE);
        if(!(serviceList.size() > 0)) {
            return false;
        }
        for(int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if(serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }
}
