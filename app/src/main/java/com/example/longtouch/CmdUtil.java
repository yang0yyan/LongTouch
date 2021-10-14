package com.example.longtouch;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CmdUtil {
    Context context;

    private int isRoot = 0;
    private boolean cmdRun = false;
    private ExecutorService fixedThreadPool = null;

    public CmdUtil(Context context) {
        this.context = context;
        isRoot = getSysRoot();
        fixedThreadPool = Executors.newFixedThreadPool(3);
        if (isRoot == 1) {
            isRoot = getAppRoot();
            if (isRoot == 2) {
                fixedThreadPool = Executors.newFixedThreadPool(3);
            }
        }
    }

    public void tap(float x, float y) {
        tap(x,y,1000);
    }

    public void tap(float x, float y, long delay) {
        if (getIsRoot() != 2) {
            MainActivity.MyLog.logFromUi(x+"  "+y);
            MainActivity.MyLog.logFromUi("未获取root");
            return;
        }
        String command = "input tap " + x + " " + y;
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cmdRun = true;
                cmdRun(command, delay);
            }
        });
    }


    public void stopTap() {
        cmdRun = false;
    }

    private void cmdRun(String command, long delay) {
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            //申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            while (cmdRun) {
                Thread.sleep(delay);
                if (!cmdRun)
                    break;
                write(command, dataOutputStream);
            }

            write("exit", dataOutputStream);
            read(process);
        } catch (Exception e) {
            Log.e("TAG", e.getMessage(), e);
            MainActivity.MyLog.logFromUi("错误：" + e.getMessage());
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                Log.e("TAG", e.getMessage(), e);
                MainActivity.MyLog.logFromUi("错误：" + e.getMessage());
            }
        }
    }

    private void write(String command, DataOutputStream dos) throws IOException {
        MainActivity.MyLog.logFromUi("命令：" + command);
        command += "\n";
        String asd = command+command+command+command+command+command+command+command+command+command;
        dos.write(asd.getBytes(StandardCharsets.UTF_8));
        dos.flush();
    }

    private void read(Process process) throws InterruptedException, IOException {
        process.waitFor();
        BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder msg = new StringBuilder();
        String line;
        // 读取命令的执行结果
        while ((line = errorStream.readLine()) != null) {
            msg.append(line);
        }
        Log.d("TAG", "install msg is " + msg);
        // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
        MainActivity.MyLog.logFromUi("结果：" + msg.toString());
    }


    public synchronized int getAppRoot() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                MainActivity.MyLog.logFromUi("应用已获取root");
                return 2;
            } else {
                MainActivity.MyLog.logFromUi("应用未获取root");
                return -2;
            }
        } catch (Exception e) {
            Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "
                    + e.getMessage());
            MainActivity.MyLog.logFromUi("错误:" + e.getMessage());
            return -2;
        } finally {
            try {
                if (os != null)
                    os.close();
                if (process != null)
                    process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getSysRoot() {
        String[] paths = {"/system/bin/", "/system/xbin/", "/system/sbin/", "sbin", "/vendor/bin/", "/su/bin"};
        for (String path : paths) {
            File file = new File(path, "su");
            if (file.exists()) {
                MainActivity.MyLog.logFromUi("手机已获取root");
                return 1;
            }
        }
        MainActivity.MyLog.logFromUi("手机未获取root");
        return -1;
    }




    public int getIsRoot() {
        return isRoot;
    }

    public void close() {
        stopTap();
        if(fixedThreadPool!=null){
            if(!fixedThreadPool.isShutdown())
                fixedThreadPool.shutdownNow();
            fixedThreadPool=null;
        }
    }
}
