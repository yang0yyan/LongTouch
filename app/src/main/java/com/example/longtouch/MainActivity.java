package com.example.longtouch;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private boolean isStart = false;
    private int tapX = 0;
    private int tapY = 0;
    private int index = 0;
    private String serviceClassName = "";
    LinkedList<String> logLines = new LinkedList<>();
    private TextView tvLog;
    private ScrollView scroll;
    FloatingService.MyBinder myBinder;
    private EditText etTimes;



    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            logFromUi("服务绑定:" + name.getClassName());
            myBinder = (FloatingService.MyBinder) service;
            serviceClassName = name.getClassName();
            myBinder.show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            logFromUi("服务解绑:" + name.getClassName());
            serviceClassName = "";
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionUtil.getPermission(this, PermissionUtil.WRITE_EXTERNAL_STORAGE);
        button = findViewById(R.id.button);
        tvLog = findViewById(R.id.tv_log);
        scroll = findViewById(R.id.scroll);
        etTimes = findViewById(R.id.et_times);
        MyLog.setActivity(this);
        FloatWindowUtil.setActivity(this);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            button.setText(String.format("%s", index++));
        } else if (view.getId() == R.id.button2) {
            index = 0;
            button.setText("点击");
            stopService(new Intent(this, FloatingService.class));
            unbindService(mConnection);
        } else if (view.getId() == R.id.button3) {
            startFloatingService(null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        MyLog.logFromUi("touch:" + x + "," + y);
        return super.onTouchEvent(event);
    }

    public void startFloatingService(View view) {
        if (!Settings.canDrawOverlays(this)) {
            MyLog.logFromUi("当前无悬浮窗权限");
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0X01);
        } else {
            startFloatService();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0X01) {
            if (!Settings.canDrawOverlays(this)) {
                MyLog.logFromUi("授权失败");
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                MyLog.logFromUi("授权成功");
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                startFloatService();
            }
        }
    }

    public void startFloatService() {
        Intent intent = new Intent(MainActivity.this, FloatingService.class);
        if (serviceClassName.equals("") || !FloatingService.isServiceExisted(this, serviceClassName)) {
            startService(intent);
            bindService(intent, mConnection, Context.BIND_IMPORTANT);
        } else {
            myBinder.show();
//            unbindService(mConnection);
//            stopService(intent);
        }
    }
    public int getTimes(){
        return Integer.parseInt(etTimes.getText().toString());
    }

    public void logFromUi(String msg) {
        logLines.add(msg);
        if (logLines.size() > 100) {
            logLines.removeFirst();
        }
        final StringBuilder stringBuffer = new StringBuilder();
        for (String line : logLines) {
            stringBuffer.append(line).append('\n');
        }
        tvLog.post(new Runnable() {
            @Override
            public void run() {
                tvLog.setText(stringBuffer.toString());
                scroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    static class MyLog {
        @SuppressLint("StaticFieldLeak")
        private static MainActivity activity;

        public static void setActivity(MainActivity activity) {
            MyLog.activity = activity;
        }

        public static void logFromUi(String msg) {
            if (activity != null)
                activity.logFromUi(msg);
        }

        public static int getTimes(){
            return activity.getTimes();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, FloatingService.class));
        unbindService(mConnection);
    }
}

