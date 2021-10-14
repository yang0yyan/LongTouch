package com.example.longtouch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class FloatView extends View {
    public FloatView(Context context) {
        super(context);
    }

    public FloatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void init(){
        Paint paint = new Paint();
        paint.setColor(0XFFFF0000);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
