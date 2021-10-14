package com.example.longtouch.floatTest;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.longtouch.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Context.WINDOW_SERVICE;

public class FloatWindowUtil {
    private Context context;
    private WindowManager windowManager;
    boolean isShow = false;
    public int windowWidth;
    public int windowHeight;
    private ExecutorService fixedThreadPool = null;

    private static List<View> listFloatView = new ArrayList<>();
    private View floatView;

    private final Point[] points = {new Point(0, -200), new Point(141, -141), new Point(200, 0), new Point(141, 141), new Point(0, 200)};
    private final Point[] points2 = {new Point(0, -100), new Point(71, -71), new Point(100, 0), new Point(71, 71), new Point(0, 100)};
    private int statusBarHeight;

    public FloatWindowUtil(Context context) {
        this.context = context;
        init();
    }

    public void init() {
        if (Settings.canDrawOverlays(context)) {
            fixedThreadPool = Executors.newFixedThreadPool(5);
            // 获取WindowManager服务
            windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);

            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            //屏幕宽
            windowWidth = displayMetrics.widthPixels;
            //屏幕高
            windowHeight = displayMetrics.heightPixels;
            //屏幕密度
            float density = displayMetrics.density;
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);

        }
    }


    public void showFloatingWindow() {
        if (Settings.canDrawOverlays(context) && null != windowManager) {
            floatView = LayoutInflater.from(context).inflate(R.layout.test_float_ball_layout, null);
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
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            layoutParams.x = 0;
            layoutParams.y = 300;
            floatView.findViewById(R.id.iv1).setOnTouchListener(new FloatingOnTouchListener(windowManager, layoutParams));
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
        }
    }


    private View showView(int x, int y) {
        if (Settings.canDrawOverlays(context) && null != windowManager) {

            // 新建悬浮窗控件
            TextView button = new TextView(context.getApplicationContext());
            button.setTag("num");
            button.setGravity(Gravity.CENTER);
            button.setBackgroundColor(0x000000FF);
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
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            layoutParams.x = x - 40;
            layoutParams.y = y - 40;
            //button.setOnTouchListener(new FloatingOnTouchListener(windowManager, layoutParams));

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


    public void addView(int x, int y, String type) {
        View view = showView(x, y);
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

    private boolean isStart = false;

    private boolean isOpen = false;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv1:
                if (listFloatView.size() == 5) {
                    for (int i = 0; i < 5; i++) {
                        if (!isOpen) {
                            AnimUtil.upAnim(points2[i], points[i], listFloatView.get(i), windowManager);
                        } else {
                            AnimUtil.downAnim(points[i], points2[i], listFloatView.get(i), windowManager);
                        }
                    }
                    isOpen = !isOpen;
                } else {
                    int[] location = new int[2];
                    v.getLocationOnScreen(location);
                    float x = location[0];
                    float y = location[1];
//                float x = v.getX();
//                float y = v.getY();
                    float width = v.getWidth();
                    float height = v.getHeight();
                    float cX = x + width / 2;
                    float cY = y + height / 2 - statusBarHeight;
                    for (Point point : points2) {
                        addView((int) cX + point.x, (int) cY + point.y, "");
                    }
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
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    if (!isClick) {
                        int movedX = nowX - x;
                        int movedY = nowY - y;
                        x = nowX;
                        y = nowY;
                        layoutParams.x = layoutParams.x + movedX;
                        layoutParams.y = layoutParams.y + movedY;
                        for(View v:listFloatView){
                            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) v.getLayoutParams();
                            layoutParams.x = layoutParams.x+movedX;
                            layoutParams.y = layoutParams.y+movedY;
                            windowManager.updateViewLayout(v, layoutParams);
                        }
                        // 更新悬浮窗控件布局
                        windowManager.updateViewLayout(floatView, layoutParams);

                    } else {
                        if (Math.abs(tapX - nowX) > 20 || Math.abs(tapY - nowY) > 20) {
                            isClick = false;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
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
}
