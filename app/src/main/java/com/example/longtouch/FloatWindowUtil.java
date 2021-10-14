package com.example.longtouch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.strictmode.DiskWriteViolation;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

public class FloatWindowUtil {
    private Context context;
    private static MainActivity activity;
    private WindowManager windowManager;
    boolean isShow = false;
    private CmdUtil cmdUtil;

    private static List<View> listFloatView = new ArrayList<>();
    private View floatView;

    public FloatWindowUtil(Context context) {
        this.context = context;
        init();
    }

    public void init() {
        if (Settings.canDrawOverlays(context)) {
            // 获取WindowManager服务
            windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        }
    }

    public void setCmdUtil(CmdUtil cmdUtil){
        this.cmdUtil = cmdUtil;
    }

    public void showFloatingWindow() {
        if (Settings.canDrawOverlays(context) && null != windowManager) {
            floatView = LayoutInflater.from(context).inflate(R.layout.float_ball_layout, null);
            // 设置LayoutParam
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            layoutParams.x = 300;
            layoutParams.y = 300;
            floatView.findViewById(R.id.btn_float_add).setOnTouchListener(new FloatingOnTouchListener(windowManager, layoutParams));
            floatView.findViewById(R.id.btn_float_start).setOnTouchListener(new FloatingOnTouchListener(windowManager, layoutParams));
            floatView.findViewById(R.id.btn_float_stop).setOnTouchListener(new FloatingOnTouchListener(windowManager, layoutParams));

            // 将悬浮窗控件添加到WindowManager
            windowManager.addView(floatView, layoutParams);
            isShow = true;
        }
    }

    public void hideFloatingWindow() {
        if (null != windowManager && isShow) {
            isShow = false;
            removeAllView();
            windowManager.removeView(floatView);
            cmdUtil.close();
        }
    }


    private View showView(String text) {
        if (Settings.canDrawOverlays(context) && null != windowManager) {

            // 新建悬浮窗控件
            TextView button = new TextView(context.getApplicationContext());
            button.setTag("num");
            button.setText(text);
            button.setGravity(Gravity.CENTER);
            button.setBackgroundColor(Color.BLUE);
            // 设置LayoutParam
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.width = 80;
            layoutParams.height = 80;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;//WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            layoutParams.x = 100;
            layoutParams.y = 100;
            button.setOnTouchListener(new FloatingOnTouchListener(windowManager, layoutParams));

            // 将悬浮窗控件添加到WindowManager
            windowManager.addView(button, layoutParams);
            return button;
        }
        return null;
    }

    private void hideView(View v) {
        if (null != windowManager) {
            windowManager.removeView(v);
        }
    }


    public void addView(String text) {
        View view = showView(text);
        listFloatView.add(view);
    }

    public void removeView(View v) {
        hideView(v);
        listFloatView.remove(v);
    }

    public void removeAllView() {
        for (View v : listFloatView)
            hideView(v);
        listFloatView.clear();
    }

    public void start() {
        if (listFloatView.size() == 0) return;
        int delay = MainActivity.MyLog.getTimes();
        if(delay<100||delay>60000){
            MainActivity.MyLog.logFromUi("频率范围无效");
            return;
        }
        MainActivity.MyLog.logFromUi("开始");
        for (View v : listFloatView) {
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            float x = location[0];
            float y = location[1];
            float width = v.getWidth();
            float height = v.getHeight();
            float tapX = x + width / 2;
            float tapY = y + height / 2;

            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) v.getLayoutParams();
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            windowManager.updateViewLayout(v, layoutParams);
            v.setBackgroundColor(0x880000FF);

            cmdUtil.tap(tapX, tapY,delay);
        }
    }

    public void stop() {
        if (listFloatView.size() == 0) return;
        MainActivity.MyLog.logFromUi("结束");
        for (View v : listFloatView) {
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) v.getLayoutParams();
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            windowManager.updateViewLayout(v, layoutParams);
            v.setBackgroundColor(0xFF0000FF);

        }
        cmdUtil.stopTap();
    }

    public boolean isStart = false;

    public void onClick(View v) {
        if ("num".equals(v.getTag())) {
            MainActivity.MyLog.logFromUi("移除");
            removeView(v);
        } else
            switch (v.getId()) {
                case R.id.btn_float_add:
                    if (listFloatView.size() >= 1) {
                        MainActivity.MyLog.logFromUi("添加到上限");
                        return;
                    }
                    MainActivity.MyLog.logFromUi("添加");
                    addView(listFloatView.size() + 1 + "");
                    break;
                case R.id.btn_float_start:
                    if (!isStart) {
                        isStart = true;
                        start();
                    }
                    break;
                case R.id.btn_float_stop:
                    if (isStart) {
                        isStart = false;
                        stop();
                    }
                    break;
            }
    }


    private class FloatingOnTouchListener implements View.OnTouchListener {
        public boolean isClick = false;
        private int tapX;
        private int tapY;
        private int x;
        private int y;
        WindowManager windowManager;
        WindowManager.LayoutParams layoutParams;

        public FloatingOnTouchListener(WindowManager windowManager, WindowManager.LayoutParams layoutParams) {
            super();
            this.windowManager = windowManager;
            this.layoutParams = layoutParams;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isClick = true;
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    tapX = (int) event.getRawX();
                    tapY = (int) event.getRawY();
                    //FloatWindowUtil.log("floatTouch-down:" + x + "," + y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    //FloatWindowUtil.log("floatTouch-move:" + x + "," + y);
                    if (!isClick) {
                        int movedX = nowX - x;
                        int movedY = nowY - y;
                        x = nowX;
                        y = nowY;
                        layoutParams.x = layoutParams.x + movedX;
                        layoutParams.y = layoutParams.y + movedY;
                        // 更新悬浮窗控件布局
                        if ("num".equals(view.getTag()))
                            windowManager.updateViewLayout(view, layoutParams);
                        else
                            windowManager.updateViewLayout(floatView, layoutParams);

                    } else {
                        if (Math.abs(tapX - nowX) > 20 || Math.abs(tapY - nowY) > 20) {
                            isClick = false;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //FloatWindowUtil.log("floatTouch-up:" + x + "," + y);
                    if (isClick) {
                        onClick(view);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    public static void setActivity(Activity activity) {
        FloatWindowUtil.activity = (MainActivity) activity;
    }


}
