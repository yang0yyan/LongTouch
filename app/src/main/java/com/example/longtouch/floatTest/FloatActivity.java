package com.example.longtouch.floatTest;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.longtouch.R;

public class FloatActivity extends AppCompatActivity {

    private WindowManager windowManager;
    private View floatView;
    private FloatWindowUtil floatWindowUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float);
        floatWindowUtil = new FloatWindowUtil(this);
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(floatWindowUtil.isShow){
                    floatWindowUtil.hideFloatingWindow();
                }else{
                    floatWindowUtil.showFloatingWindow();
                }
            }
        });
    }
}